package de.rsp.wdntxml.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.rsp.wdntxml.structure.Lexicon;
import de.rsp.wdntxml.structure.Relation;
import de.rsp.wdntxml.structure.Synset;
import de.rsp.wdntxml.structure.Word;
import de.rsp.wdntxml.structure.Wordnet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Output {

	public static void outRelFile(Wordnet wordnet, String outDir) {

		try {

			List<String> relList = new ArrayList<String>();

			File file = new File(outDir + wordnet.getLang() + "RelList.out");
			System.out.println("Writing RelList to file: " + file.getAbsolutePath());
			PrintWriter write = new PrintWriter(file, "UTF-8");

			for (Synset syn : wordnet.getSynsetList()) {

				for (Relation rel : syn.getRelations()) {

					if (!relList.contains(rel.getType())) {
						relList.add(rel.getType());
						write.write(rel.getType() + "\n");
					}
				}
			}

			write.close();

		} catch (IOException e) {
			System.out.println("Failed writing relList to file.");
			e.printStackTrace();
		}
	}

	public static void outData(Lexicon lex, String outDir) {

		outToFileWordList(lex, outDir);
		outToFileSynsetList(lex, outDir);
	}

	public static void outToDB(Wordnet wordnet, String databaseUrl, String user, String pw, int port, String sshUser,
			String sshPw, int sshPort) throws SQLException, ClassNotFoundException {

		long time = System.currentTimeMillis();

		// ***// enable ssh connection via portforwarding
		
		int assinged_port = 0;
		Session session = null;
		Connection con = null;
		Statement st = null;

		try {
			int lport;
			String rhost;
			int rport;

			JSch jsch = new JSch();

			// String host = "aspra8.informatik.uni-leipzig.de";

			session = jsch.getSession(sshUser, databaseUrl, sshPort);

			lport = 34090;
			rhost = databaseUrl;
			rport = port;

			session.setPassword(sshPw);
			session.setConfig("StrictHostKeyChecking", "no"); // TODO create a
																// known_hosts
																// file and link
																// it
//			System.out.println(sshUser + "@" + databaseUrl + ":" + sshPort + "\n" + sshPw);
			session.connect();

			assinged_port = session.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:" + assinged_port + " -> " + rhost + ":" + rport);

			// ***//

			System.out.println("start uploading to database: rsei_dict_" + wordnet.getLang() + " ...");

			// ***// Create a Database
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:" + assinged_port, user, pw);
			st = con.createStatement();

			try {
				// drop database to get rid of the old version of it.
				st.executeUpdate("DROP DATABASE rsei_dict_" + wordnet.getLang().toLowerCase());
			} catch (SQLException e) {

			}

			st.executeUpdate("CREATE DATABASE rsei_dict_" + wordnet.getLang().toLowerCase());
			st.close();
			con.close();
			// ***//

			// ***// Connect to created Database
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:" + assinged_port + "/rsei_dict_" + wordnet.getLang(), user, pw);
			st = con.createStatement();
			// ***//

			// ***// Create new Tables
			st.executeUpdate("CREATE TABLE wordnet_words (wordID CHAR(6), synsetID CHAR(8), word TEXT, pos CHAR(1));");
			st.executeUpdate("CREATE TABLE wordnet_rels (synsetID1 CHAR(8), synsetID2 CHAR(8), relation VARCHAR(4));");
			st.executeUpdate("CREATE TABLE wordnet_defs (synsetID CHAR(8), definition TEXT);");
			st.executeUpdate("CREATE TABLE wordnet_examples (synsetID CHAR(8), example TEXT);");
			st.executeUpdate("CREATE TABLE wordnet_relmap (relType VARCHAR(4), relMapping TEXT, relDescription TEXT);"); //TODO datentypen einschränken
			// ***//

			// ***// write in data
			for (Synset syn : wordnet.getSynsetList()) {

				String sid = syn.getiD();

				// ***// words data
				for (Word word : syn.getWords()) {
					st.executeUpdate("INSERT INTO wordnet_words (wordID, synsetID, word, pos) VALUES (\'"
							+ word.getWordID() + "\', \'" + sid + "\', \'" + word.getWrittenForm() + "\', \'"
							+ syn.getPos() + "\');");
				}

				// ***// rels data
				for (Relation rel : syn.getRelations()) {
					st.executeUpdate("INSERT INTO wordnet_rels (synsetID1, synsetID2, relation) VALUES (\'" + sid
							+ "\', \'" + rel.getTargetSynset() + "\', \'" + rel.getType() + "\');");
				}

				// ***// defs data
				if (syn.getDefinition() != null)
					st.executeUpdate("INSERT INTO wordnet_defs (synsetID, definition) VALUES (\'" + sid + "\', \'"
							+ syn.getDefinition().replaceAll("\'", "\'\'") + "\');");

				// ***// examples data
				for (String example : syn.getExample()) {
					if (example != null)
						st.executeUpdate("INSERT INTO wordnet_examples (synsetID, example) VALUES (\'" + sid + "\', \'"
								+ example.replaceAll("\'", "\'\'") + "\');");
				}
								
			}
			// ***// relation mapping data
			try {
				BufferedReader reader = new BufferedReader(
						new FileReader(new File(wordnet.getPathToRelationMapping())));

				String line = "";

				while ((line = reader.readLine()) != null) {

					String[] partLine = line.split("/");
					// System.out.println(line + "\n" + partLine[0] +
					// partLine[1] + partLine[2]);
					st.executeUpdate("INSERT INTO wordnet_relmap (relType, relMapping, relDescription) VALUES (\'"
							+ partLine[0] + "\', \'" + partLine[1] + "\', \'" + partLine[2] + "\')");
				}
				reader.close();

			} catch (FileNotFoundException e) {
				System.err.println("Relation Mapping file was not found! Table could not be filled with data.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Couldn't read Relation Mapping file! Table could not be filled with data.");
				e.printStackTrace();
			}

			// ***//
			System.out.println(
					"Database upload finished after " + (System.currentTimeMillis() - time) / 1000 + " seconds.");

		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			// ***// Close Databse Connection
			st.close();
			con.close();
			session.disconnect();
			System.out.println("Database connection closed.");
			// ***//
		}
	}

	public static void outToFileWordList(Lexicon lex, String outDir) {

		try {

			File file = new File(outDir + lex.getLang() + "WordList.out");
			System.out.println("Writing WordList to file: " + file.getAbsolutePath());
			PrintWriter write = new PrintWriter(file, "UTF-8");

			for (Word w : lex.getWordList()) {
				write.write(w.getWrittenForm() + " " + w.getPartOfSpeech() + "\n");
			}

			write.close();

		} catch (IOException e) {
			System.out.println("Failed writing wordlist to file.");
			e.printStackTrace();
		}
	}

	public static void outToFileSynsetList(Lexicon lex, String outDir) {

		try {
			File file = new File(outDir + lex.getLang() + "SynsetList.out");
			System.out.println("Writing Synsetlist to file: " + file.getAbsolutePath());
			PrintWriter write = new PrintWriter(file, "UTF-8");

			for (Synset s : lex.getSynsetList()) {
				write.write(s.getiD() + " - " + s.getPos() + "\n" + s.getDefinition() + "\n" + s.getExample() + "\n[");
				for (Word w : s.getWords()) {
					write.write("[" + w.getWrittenForm() + "] ");
				}
				write.write("]\n[");
				for (Relation rel : s.getRelations()) {
					write.write("[" + rel.getTargetSynset() + ", " + rel.getType() + "] ");
				}
				write.write("]\n\n");
			}

			write.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ObservableList<String> outTextSynsetList(Lexicon lex) {

		List<String> list = new ArrayList<String>();
		ObservableList<String> ol = FXCollections.observableList(list);
		String item = "";
		for (Synset s : lex.getSynsetList()) {
			item += ((s.getiD() + " - " + s.getPos() + "\n" + s.getDefinition() + "\n" + s.getExample() + "\n["));
			for (Word w : s.getWords()) {
				item += (("[" + w.getWrittenForm() + "] "));
			}
			item += (("]\n["));
			for (Relation rel : s.getRelations()) {
				item += (("[" + rel.getTargetSynset() + ", " + rel.getType() + "] "));
			}
			item += (("]\n\n"));
			ol.add(item);
		}
		return ol;
	}

	/**
	 * Outputs a Lexicon to rspXMLFile
	 * 
	 * @param lex
	 *            Lexicon to output
	 * @param outDir
	 *            location to output
	 */
	public static void outToFileXmlFile(ObservableList<Synset> synsetList, String lang, String outDir) {

		// <Lexicon>
		// <Synset ID=001201043 POS=n LANG=ENG>
		// <Words>
		// <Word ID=012345>word1</Word>
		// <Word ID=012346>word2</Word>
		// </Words>
		// <Relations>
		// <Relation TYPE=hypo>001241241</Relation>
		// <Relation TYPE=syno>001243292</Relation>
		// </Relations>
		// <Definition>this is a Definition.</Definition>
		// <Examples>
		// <Example>example1</Example>
		// <Example>example2</Example>
		// </Examples>
		// </Synset>
		// <Synset ...>
		// ...
		// </Synset>
		// </Lexicon>

		Element root = new Element("Lexicon");
		root.setAttribute("LANG", lang);

		for (Synset syn : synsetList) {

			Element synset = new Element("Synset");
			List<Attribute> ats = new ArrayList<Attribute>();
			ats.add(new Attribute("ID", syn.getiD()));
			ats.add(new Attribute("POS", syn.getPos()));
			synset.setAttributes(ats);

			Element words = new Element("Words");
			for (Word w : syn.getWords()) {

				Element word = new Element("Word");
				List<Attribute> wats = new ArrayList<Attribute>();
				wats.add(new Attribute("ID", w.getWordID()));
				word.setAttributes(wats);
				word.addContent(w.getWrittenForm());
				words.addContent(word);
			}
			synset.addContent(words);

			Element relations = new Element("Relations");
			for (Relation r : syn.getRelations()) {

				Element relation = new Element("Relation");
				relation.setAttribute("TYPE", r.getType());
				relation.addContent(r.getTargetSynset());
				relations.addContent(relation);
			}
			synset.addContent(relations);

			Element definition = new Element("Definition");
			definition.addContent(syn.getDefinition());
			synset.addContent(definition);

			Element examples = new Element("Examples");
			for (String ex : syn.getExample()) {

				Element example = new Element("Example");
				example.addContent(ex);
				examples.addContent(example);
			}
			synset.addContent(examples);

			root.addContent(synset);
		}

		Document doc = new Document(root);

		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());

		try {
			xmlOut.output(doc,
					new OutputStreamWriter(
							new FileOutputStream(new File(outDir + File.separator + "rspXML_" + lang + ".xml")),
							Charset.forName("utf-8")));
			System.out.println("Xml File saved to: " + outDir + File.separator + "rspXML_" + lang + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
