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
package dot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import executer.Executer;

/**
 * Actually write files.
 * @author tibecker
 */
public class Execute {
	private static List<String> formatList;
	public static List<String> getAvailableFormats () {
		if (formatList == null) {
			Executer exe = new Executer("dot");
			if (!exe.binAvailable()) {
				throw new RuntimeException("dot not found!");
			}

			exe.execute("-Thelp");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					exe.getErrorStream()));
			StringBuffer buf = new StringBuffer();
			String tmp;
			try {
				while ((tmp = reader.readLine()) != null) {
					buf.append(tmp);
				}
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
			buf.delete(0, "Format: \"help\" not recognized. Use one of:"
					.length());
			StringTokenizer tok = new StringTokenizer(buf.toString().trim());
			formatList = new LinkedList<String>();
			while (tok.hasMoreElements()) {
				formatList.add(tok.nextToken());
			}
		}

		return formatList;
	}

	public static void writeGif(BaseGraph graph, String fileName) {
		write(graph, "gif", fileName);
	}

	public static void writeGif(BaseGraph graph, OutputStream out) {
		write(graph, "gif", out);
	}
	public static void writePng(BaseGraph graph, String fileName) {
		write(graph, "png", fileName);
	}

	public static void writePng(BaseGraph graph, OutputStream out) {
		write(graph, "png", out);
	}
	public static void writeJpeg(BaseGraph graph, String fileName) {
		write(graph, "jpeg", fileName);
	}

	public static void writeJpeg(BaseGraph graph, OutputStream out) {
		write(graph, "jpeg", out);
	}
	public static void writeSvg(BaseGraph graph, String fileName) {
		write(graph, "svg", fileName);
	}

	public static void writeSvg(BaseGraph graph, OutputStream out) {
		write(graph, "svg", out);
	}
	public static void writePs(BaseGraph graph, String fileName) {
		write(graph, "ps", fileName);
	}

	public static void writePs(BaseGraph graph, OutputStream out) {
		write(graph, "ps", out);
	}
	public static void writePdf(BaseGraph graph, String fileName) {
		write(graph, "pdf", fileName);
	}

	public static void writePdf(BaseGraph graph, OutputStream out) {
		write(graph, "pdf", out);
	}

	public static void write(BaseGraph graph, String format, String fileName){
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			write(graph, format, out);
		} catch (FileNotFoundException e) {
			System.err.println("I'm afraid you can't do that, Dave.");
			e.printStackTrace();
		} finally {
			try {if (out!=null)out.close();} catch (Throwable t) {throw new RuntimeException("ARRG! Run around screaming and waving your arms!!");}
		}
	}
	public static void write(BaseGraph graph, String format, OutputStream out){
		format = format.toLowerCase();
		if (!getAvailableFormats().contains(format)) {
			throw new RuntimeException("Format: "+format+" not available!");
		}

		String binName = "neato";
		if (graph instanceof Digraph) {
			binName = "dot";
		}

		Executer exe = new Executer(binName);
		exe.execute("-T"+format);


		OutputStreamWriter writer = new OutputStreamWriter(exe.getOutputStream());

		try {
			writer.write(graph.pack());
			writer.close();

			moranCopy(exe.getInputStream(), out);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void moranCopy(InputStream inputStream, OutputStream out) throws IOException {
		byte[] b = new byte[1024];
		int count=0;
		while ((count = inputStream.read(b)) != -1) {
			out.write(b, 0, count);
		}
	}
	public static void main(String [] args){
		List<String> l = Execute.getAvailableFormats();
		for (String s:l){
			System.out.println(s);
		}
		System.out.println("--");
	}
}
