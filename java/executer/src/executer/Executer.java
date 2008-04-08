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

package executer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
 * This is a wee little utility to help you out in those moments where
 * you just can't help yourself and just _have_ to call
 * 	Runtime.getRuntime().exec
 * 
 * Which you're not supposed to do because it awful and the little baby
 * Duke will cry, but tough. This utility makes it easier to deal with
 * "cross-plattform" issues that come up when calling an external
 * process, i.e. finding the proper binary to call.
 *
 * Say you want to call `dot` from the Graphwiz package... All you need
 * to do is:
 *
 * <code>
 * String [] dotArgs = [ ... args to pass dot ...];
 * Executer exe = new Executer("dot");
 * exe.execute("dot");
 * </code>
 *
 * and `Executer` handles the rest, i.e. searching the PATH and
 * optionally recursing through %PROGRAM_FILES% for the proper binary.
 *
 */

public class Executer extends Process {

	File bin;

	private boolean traverseProgramFiles;

	private Process proc;

	private boolean dontDumpToScreen = true;

	/**
	 *
	 * @param bin
	 * @param traverseProgramFiles
	 *            Whether to traverse the %ProgramFiles% hierarchy searching for
	 *            the executable. This may take a long time...
	 */
	public Executer(String bin, boolean traverseProgramFiles) {
		bin = bin.trim();
		this.traverseProgramFiles = traverseProgramFiles;
		this.bin = findBin(bin);
	}

	/**
	 * Create an executer for binary named `bin`. If this binary is not found in
	 * the path, don't search `ProgramFiles`. If you'd like to search
	 * %ProgramFiles% use the other constructor.
	 *
	 * @param bin
	 */
	public Executer(String bin) {
		this(bin, false);
	}

	/**
	 * returns whether the Executor found a suitable binary.
	 *
	 * @return
	 */
	public boolean binAvailable() {
		return bin != null;
	}

	public String binName() {
		return bin.getAbsolutePath();
	}

	public void setDontDumpToScreen(boolean dontDump) {
		this.dontDumpToScreen = dontDump;
	}

	public boolean execute(String... args) {
		String[] actual_args = new String[args.length + 1];
		System.arraycopy(args, 0, actual_args, 1, args.length);
		actual_args[0] = this.binName();
		try {
			this.proc = Runtime.getRuntime().exec(actual_args);
			if (!dontDumpToScreen) {
				dump(proc.getErrorStream());
				dump(proc.getInputStream());

			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void dump(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line=null;
		while ((line=reader.readLine())!=null){
			System.out.println(line);
		}
	}

	final static String[] EMPTY = new String[0];

	public boolean execute() {
		return execute(EMPTY);
	}

	private void env() {
		Map<String, String> env = System.getenv();
		for (String s : env.keySet()) {
			System.out.println(String.format("%s : %s", s, env.get(s)));
		}
	}

	private File findBin(String bin) {

		String os = System.getenv("OS");
		if (os != null && os.indexOf("Windows") != -1) {
			return findBinWindows(bin);
		}

		return findBinNix(bin);

	}

	private File findBinNix(String bin) {
		return checkPathFor(bin, ":");
	}

	private File findBinWindows(String bin) {
		if (bin != null && !bin.endsWith(".exe")) {
			bin = bin + ".exe";
		}

		// first check the path
		File f = checkPathFor(bin, ";");

		// if nothing was found and user requested it,
		// search across Program Files
		if (f == null && this.traverseProgramFiles) {
			String p_files = System.getenv("ProgramFiles");
			f = traverse(p_files, bin);
		}
		return f;
	}

	private File traverse(String p_files, final String bin) {
		File baseDir = new File(p_files);
		if (!baseDir.isDirectory())
			return null;

		File f = null;
		if ((f = checkDirectory(baseDir, bin)) != null) {
			return f;
		}

		File[] dirs = baseDir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		});

		for (File f1 : dirs) {
			f = traverse(f1.getAbsolutePath(), bin);
			if (f != null)
				return f;
		}

		return null;
	}

	private File checkDirectory(File dir, final String bin) {

		if (!dir.isDirectory()) { // sanity
			// System.out.println("Not a directory:"+dir);
			return null;
		}

		File[] exes = dir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				// this does the actual check: the bin.endsWith
				// is because a match should be found if user requests
				// e.g. bin/sh located in /usr/bin/sh.
				return bin.endsWith(arg0.getName()); // only in 1.6?  && arg0.canExecute();
			}
		});

		// System.out.println(String.format("Checkin dir %s for %s and got %d",
		// dir.toString(), bin, exes.length));
		// for (File f:exes) {
		// System.out.println("!!!"+f);
		// }
		if (exes.length != 0)
			return exes[0];

		return null;

	}

	private File checkDirectory(String string, String bin2) {
		return checkDirectory(new File(string), bin2);
	}

	private File checkPathFor(String bin, String sep) {
		String path = System.getenv("PATH");
		if (path == null)
			return null;

		StringTokenizer tok = new StringTokenizer(path, sep);
		String t = null;
		File f = null;
		while (tok.hasMoreElements()) {
			t = cleanup(tok.nextToken());
			if (t != null && (f = checkDirectory(t, bin)) != null)
				return f;
		}
		return null;
	}

	private String cleanup(String t) {
		// System.out.println(t);
		// on occasion, windows paths are surrounded by quotes,
		// clean up. This isn't necessary for `traverse` because
		// none of the dirs are user provided.
		Pattern p = Pattern.compile("^\"?(.*?)\"?$");
		Matcher m = p.matcher(t);
		if (!m.matches())
			return null;
		t = m.group(1);
		// System.out.println(t);
		return t;
	}

	public static void main(String[] args) {
		Executer exe = new Executer("dot");
		System.out.println(exe.binAvailable());
		System.out.println(exe.binName());
		//String [] arg = {"--help"};
		//System.out.println(exe.execute(arg));
		System.out.println(exe.execute());
		exe.waitFor();
		System.out.println(exe.exitValue());
	}

	// Implementation of the methods defined by java.lang.Process,
	// these are just wrapped around the Process object returned
	// by `java.lang.Runtime.exec()` so it's a bit boring from here on.

	@Override
	public void destroy() {
		_check();
		this.proc.destroy();
	}

	@Override
	public int exitValue() {
		_check();
		return this.proc.exitValue();
	}

	@Override
	public InputStream getErrorStream() {
		_check();
		return this.proc.getErrorStream();
	}

	@Override
	public InputStream getInputStream() {
		_check();
		return this.proc.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		_check();
		return this.proc.getOutputStream();
	}

	@Override
	public int waitFor() {
		_check();
		int i = -1;
		try {
			i = this.proc.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

	private void _check() {
		final class YoureDoingItWrongException extends RuntimeException {
			public YoureDoingItWrongException(String txt) {
				super(txt);
			}
		}
		;
		if (this.proc == null) {
			throw new YoureDoingItWrongException(
					"You're doing it wrong! Call `execute` first!");
		}
	}

}
