package com.SZZ.jiraAnalyser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import com.SZZ.jiraAnalyser.entities.*;
import com.SZZ.jiraAnalyser.git.JiraRetriever;

public class Application {
	
	
	
	public  URL sourceCodeRepository;
	public  URL bugTracker;
	
	private final TransactionManager transactionManager = new TransactionManager();
	private final LinkManager linkManager = new LinkManager();
    public boolean hasFinished = false;
    
    private String projectName;
	
    
    public Application(){}
		
	
	public boolean mineData(String git, String jira, String projectName, String token) throws MalformedURLException {
		this.sourceCodeRepository = new URL(git);
		this.bugTracker = new URL(jira);
		this.projectName = projectName;
		
		try {
		
		System.out.println("Downloading Git logs for project " + projectName);
		List<Transaction> transactions = transactionManager.getBugFixingCommits(sourceCodeRepository,projectName);
		System.out.println("Git logs downloaded for project " + projectName);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  false;
		}
		System.out.println("Calculating bug fixing commits for project " + projectName);
		List<Link> links = linkManager.getLinks(transactions, projectName, null);
		printData(links);
		discartLinks(links);
		saveBugFixingCommits(links,projectName);
		System.out.println("Bug fixing commits for project " + projectName + "calculated");
		System.out.println(links.size()+" bug fixing commits for project " + projectName + "found");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return  false;
		}
		System.out.println("Calculating Bug inducing commits for project " + projectName);
		calculateBugInducingCommits(links,projectName,token);
		System.out.println("Bug inducing commits for project calculated");
		}
		catch(Exception e){
			return  false;
		}

		return  true;
	}
	
	/**
	 * It prints a table summarizing the results of the analysis
	 * @param links
	 */
	private void printData(List<Link> links){
		int[][] multi = new int[4][7];	
		for (int row = 0; row < 4; row ++)
		    for (int col = 0; col < 7; col++)
		    	multi[row][col] = 0;
		multi[0][0]  = 0;	
		multi[1][0]  = 1;	
		multi[2][0]  = 2;
		
		for (Link l : links){
			int row = l.getSyntacticConfidence();
			int column = l.getSemanticConfidence();
			column++;
			multi[row][column]++;	
			multi[row][6]++;
			multi[3][column]++;
			multi[3][6]++;
		}
		
		String print = "\n";
		print += String.format("%-16s%-16s%-16s%-16s%-16s%-16s%-16s","syn / sem", "0", "1", "2", "3", "4","total");
		print += "\n";
		print += String.format("%s", "--------------------------------------------------------------------------------------------------------------");
		print += "\n";
		print += String.format("%-16d%-16d%-16d%-16d%-16d%-16d%-16d", multi[0][0], multi[0][1], multi[0][2], multi[0][3], multi[0][4], multi[0][5],multi[0][6]);
		print += "\n";
		print += String.format("%-16d%-16d%-16d%-16d%-16d%-16d%-16d", multi[1][0], multi[1][1], multi[1][2], multi[1][3], multi[1][4], multi[1][5],multi[1][6]);
		print += "\n";
		print += String.format("%-16d%-16d%-16d%-16d%-16d%-16d%-16d", multi[2][0], multi[2][1], multi[2][2], multi[2][3], multi[2][4], multi[2][5],multi[2][6]);
		print += "\n";
		print += String.format("%s", "--------------------------------------------------------------------------------------------------------------");
		print += "\n";
		print += String.format("%-16d%-16d%-16d%-16d%-16d%-16d%-16d", multi[3][0], multi[3][1], multi[3][2], multi[3][3], multi[3][4], multi[3][5],multi[3][6]);
		System.out.println(print);
	}
	
	/*
	 * Only Links with sem > 1 OR ( sem = 1 AND syn > 0) must be considered
	 */
	private void discartLinks(List<Link> links){
		List<Link> linksToDelete = new LinkedList<Link>();
		for (Link l : links) {
            if (l.issue != null && !l.issue.getType().equals("Bug")
                    || !(l.getSemanticConfidence() > 1 || (l.getSemanticConfidence() == 1 || l.getSyntacticConfidence() > 0))
                    || l.transaction.getTimeStamp().getTime() > l.issue.getClose()) {
                linksToDelete.add(l);
            }
		}
		String print = "\n";
		print += "\n";
		print += String.format("%s", "--------------------------------------------------------------------------------------------------------------");
		print += "\n";
		print+=("Links removed too low score (sem > 1 v (sem = 1 and syn > 0)): "+ linksToDelete.size() +" ("+ ((double)linksToDelete.size()/(double)links.size())*100 + "%)");
		System.out.println(print);
		links.removeAll(linksToDelete);
	}
	
	/**
	 * It saves all bug fixing commits found on a file
	 * @param links
	 * @param projectName
	 */
	private void saveBugFixingCommits(List<Link> links,String projectName){
		try {
			PrintWriter printWriter = new PrintWriter(new File( projectName+"_BugFixingCommit.csv"));
			printWriter.println("commitsSha;commitTs;commitComment;issueKey;issueOpen;issueClose;issueTitle");
			String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		    SimpleDateFormat format = new SimpleDateFormat(pattern);
			for (Link l : links){
				String row = l.transaction.getId() + ";"
						+    format.format(l.transaction.getTimeStamp()) + ";"
						+    l.transaction.getComment() + ";"
						+    projectName+"-"+l.issue.getId()	+";"
						+    format.format(new Date(l.issue.getOpen())) + ";"
					    +    format.format(new Date(l.issue.getClose())) + ";"
					    +    l.issue.getTitle()
						;
				printWriter.println(row);				
			}
			printWriter.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		
		private void calculateBugInducingCommits(List<Link> links,String projectName, String token){
			System.out.println("Calculating Bug Inducing Commits");
			int count = links.size();
			PrintWriter printWriter;
			try {
				printWriter = new PrintWriter(token+"_BugInducingCommits.csv");
				printWriter.println("bugFixingId;bugFixingTs;bugFixingfileChanged;bugInducingId;bugInducingTs;issueId");
				for (Link l : links){
					if (count % 100 == 0)
						System.out.println(count + " Commits left");
					l.calculateSuspects(transactionManager.getGit(),null);
					String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
			        SimpleDateFormat format1 = new SimpleDateFormat(pattern);
			        for (Suspect s : l.getSuspects()){
			        	printWriter.println();
			        	printWriter.println(
			        			l.transaction.getId() + ";" + 
			        			format1.format(l.transaction.getTimeStamp()) +";" +
			        			s.getFileName()		+ ";" +
			        			s.getCommitId()     + ";" +
			        			format1.format(s.getTs()) +";"+
			        			l.issue.getId()
			        			);
			        }
			        count--;
			}
				printWriter.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println((e.getStackTrace()));
			}	

		
	}
}
