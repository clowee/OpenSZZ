package com.SZZ.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.util.FileUtils;

import com.SZZ.jiraAnalyser.Application;
import com.SZZ.jiraAnalyser.entities.Transaction;
import com.SZZ.jiraAnalyser.entities.TransactionManager;
import com.SZZ.jiraAnalyser.git.Git;
import com.SZZ.jiraAnalyser.git.JiraRetriever;

public class SZZApplication {

	/* Get actual class name to be printed on */
	
	private static String jiraAPI = "/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";

	public static void main(String[] args) {
		args = new String[7];
		args[0] = "-all";
		args[1] = ""; //"https://github.com/apache/commons-bcel.git";
		args[2] = "";//"https://issues.apache.org/jira/projects/BCEL";
		args[3] = "";// BCEL";
		

		if (args.length == 0) {
			System.out.println("Welcome to SZZ Calculation script.");
			System.out.println("Here a guide how to use the script");
			System.out.println("szz.jar -all githubUrl, jiraUrl, jiraKey => all steps together");
		} else {
			switch (args[0]) {
			case "-all":
				Git git;
				try {
					String[] array = args[2].split("/jira/projects/");
					String projectName = args[3];
					String jiraUrl = array[0] + jiraAPI;
					JiraRetriever jr1 = new JiraRetriever(jiraUrl, projectName);
					jr1.printIssues();

				} catch (Exception e) {
					break;
				}
				try {
					Application a = new Application();
					a.mineData(args[1], args[2].replace("{0}", args[3]), args[3], args[3]);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				clean(args[3]);
				break;
			default:
				System.out.println("Commands are not in the right form! Please retry!");
				break;

			}
		}

	}

	private static void clean(String jiraKey) {
		for (File fileEntry : new File(".").listFiles()) {
			if (fileEntry.getName().toLowerCase().contains(jiraKey.toLowerCase())) {
				if (!fileEntry.getName().contains("Commit")) {
					try {
						if (fileEntry.isFile())
							Files.deleteIfExists(fileEntry.toPath());
						else
							deleteDir(fileEntry);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}
}
