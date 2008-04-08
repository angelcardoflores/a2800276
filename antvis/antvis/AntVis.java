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
// 
package antvis;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import cmdline.CmdLine;

import dot.Digraph;
import dot.Edge;
import dot.Execute;
import dot.Node;


/**
 * Tiny little helper application to help visualize the dependancies
 * within ant build files.
 * Dependancy graphs are rendered using Graphviz, which should be
 * installed to run. You can get Graphviz at www.graphviz.org. 
 */

public class AntVis {

	static class TwoString {
		String one;
		String two;
		TwoString(String one, String two) {
			this.one=one;
			this.two=two;
		}
	}
	private static CmdLine cmd;
	public static void main(String [] args) throws Throwable {
		handleArgs(args);
		if (cmd.exists("-l")) { // list output formats supported by graphviz.
			for (String s : Execute.getAvailableFormats()){
				System.out.println(s);
			}
			System.exit(0);
		}


		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();

		final HashMap<String, Node> nodes = new HashMap<String, Node>();
		final HashSet<TwoString> deps = new HashSet<TwoString>();

		parser.parse(cmd.get("-f"), new DefaultHandler() {
			public void startElement(String uri, String localName, String qname, Attributes s) {
				if ("target".equalsIgnoreCase(qname)) {
					String name = s.getValue("name");
					if (null == name) {
						throw new RuntimeException("WTF!?");
					}
					nodes.put(name, new Node(name));

					String d = s.getValue("depends");

					if (null != d) {
						StringTokenizer tok = new StringTokenizer(d, ",");
						while (tok.hasMoreTokens()) {
							String from = name.trim();
							String to = tok.nextToken().trim();
							//System.out.println(from+":"+to);
							deps.add(new TwoString(from, to));
						}
					}

				}
			}
		});

		Digraph graph = new Digraph();
		// handle deps
		for (Iterator<TwoString> it = deps.iterator(); it.hasNext();) {
			TwoString str = it.next();
			graph.addEdge(new Edge(nodes.get(str.one), nodes.get(str.two)));
		}

		for (Node n : nodes.values()) {
			graph.addNode(n);
		}

		OutputStream os = System.out;
		if (cmd.get("-o")!=null) {
			os = new FileOutputStream(cmd.get("-o"));
		}

		String format = "dot";
		if (cmd.get("-t")!=null) {
			format=cmd.get("-t");
		}


		Execute.write(graph, format, os);


	}
	private static void handleArgs(String[] args) {
		cmd = new CmdLine(args);

		if ((cmd.get("-f")==null || "".equals(cmd.get("-f"))) && !cmd.exists("-l") ) {
			usage();
			System.exit(1);
		}
	}
	private static void usage() {
		System.err.println("usage: [jre] antvis.AntVis -f inputFile [-t format] [-o outfile]\n" +
				"\tformat: format supported by dot. Default: `dot`\n" +
				"\toutfile: Default stdout\n" +
				"call [jre] antvis.AntVis -l for a list of supported formats");

	}
}
