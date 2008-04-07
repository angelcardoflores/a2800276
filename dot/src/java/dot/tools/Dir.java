package dot.tools;

import dot.*;

import java.io.*;
/*
	Renders directory Structur in dot.
*/

public class Dir {
	File startDir;
	Graph g = new Graph();
	int count;
	
	public Dir (File startDir) {
		this.startDir = startDir;
		Node start = new Node (Integer.toString(count++), startDir.getName());
		g.addNode (start);
		handleSubdirectories(start, startDir);
	}

	void handleSubdirectories (Node parent, File start) {
		File [] files = start.listFiles ( 
			new FileFilter () {
				 public boolean accept(File file) {
					return file.isDirectory();
				 }
			}
		);
		
		Node curr;
		for (int i = 0; i!=files.length; ++i) {
			curr = new Node (Integer.toString(count++), files[i].getName());
			g.addNode(curr);
			g.addEdge(new Edge (parent, curr));
			handleSubdirectories(curr, files[i]);
		}
	}

	String renderImage () {
		return g.pack();	
	}
	

	private static void usage () {
		System.err.println ("usage: [jre] dotDir <startDirectory>");
		System.exit(1);
	}
	public static void main (String [] args) {
		if (args.length!=1)
			usage();
		Dir dir = null;
		try {
			dir = new Dir (new File (args[0]));	
		} catch (Throwable t){
			t.printStackTrace();
			usage();
		}

		System.out.println (dir.renderImage());
	}	
}
