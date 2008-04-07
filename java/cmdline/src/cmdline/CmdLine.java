package cmdline;



/**
 Quick utility class to facilitate working with commandline
 arguments passed to the main method of an application. This is a
 rather rudementary implementation that won't parse any really
 fancy cmd line flag type things only the following<p>
 <pre>
 -flagName flagValue
 </pre>
 <p>
 In other words, a flag preceded by a dash ('-') is necessary as
 well as an option name. Standalone flags won't work in this
 implementation.

 @author tim@kuriositaet.de


 */
import java.util.*;

public class CmdLine {

        private Hashtable hash = new Hashtable();

        private String[] args;

        /**
         * Initialise these CmdLine Parameters with the given commandline. This
         * should be the commandline arguments provided to the main method in
         * (String [] args)
         */
        public CmdLine(String[] cmdLine) {
                this.args = cmdLine;
                parse();
        }

        /**
         * The method that handles parsing the cmd line argument array.
         */
        private void parse() {
                String option = null;
                for (int i = 0; i < args.length; ++i) {
                        if (!args[i].startsWith("-")) {
                                // always have to have a flag
                                continue;
                        }
                        option = args[i].substring(0, args[i].length());

                        ++i;
                        if (i >= args.length) {
                                // we're done
                        		hash.put(option, "");
                                break;
                        }
                        if (args[i].startsWith("-")) {
                                // this is another option
                                --i;
                                continue;
                        }
                        hash.put(option, args[i]);
                }

        }

        /**
         * Retrieve the option with the provided name, returns null if the option
         * was not set.
         */
        public String get(String option) {
                return (String) hash.get(option);
        }

        /**
         * Make sure all the provided options are set and contains a value.
         * @param strings
         * @return
         */
        public boolean ensure(String...options){
        	for (String s : options) {
        		if (get(s)==null){
        			return false;
        		}
        	}
        	return true;
        }

        /**
         * Check whether a parameter of the given name was set at all.
         * @param option
         * @return
         */
        public boolean exists(String option) {
                return hash.containsKey(option);
        }

        /**
         * Retrieve an `int` parameter named `option`.
         * @param option the name of the parameter to retrieve
         * @param def the default value in case the parameter wasn't set.
         * @return
         */
        public int getInt(String option, int def) {
                if (get(option) == null)
                        return def;
                int ret = def;
                try {
                        ret = Integer.parseInt(get(option));
                } catch (NumberFormatException nfe) {

                }
                return ret;
        }
}

