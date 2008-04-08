// Copyright (c) 2008 Tim Becker (tim.becker@gmx.net)
// 
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

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
