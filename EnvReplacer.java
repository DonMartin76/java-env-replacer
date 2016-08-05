import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Static class containing utilities for replacing environment variables
 * in files and lists of strings.
 */
public class EnvReplacer {
    /**
     * Replaces environment variables in a {@link List} of Strings.
     * As a second step, it will unescape any backslashed characters
     * in the string. If you have a substring like \${HOME}, this will
     * be rendered to ${HOME}, while ${HOME} will be replaced with the content
     * of the HOME environment variable.
     * 
     * @param   lines   The List of Strings to replace env vars in
     */
    public static List<String> replaceEnv(List<String> lines) {
        Map<String, String> envVars = System.getenv();
        List<String> outLines = new ArrayList<String>();

        for (String line : lines) {
            String tempLine = line;
            for (String envVar : envVars.keySet()) {
                tempLine = replaceVar(tempLine, envVar, envVars.get(envVar));
            }
            tempLine = unescape(tempLine);
            outLines.add(tempLine);
        }
        return outLines;
    }

    private static String replaceVar(String s, String name, String value) {
        String temp = s;
        String findName = String.format("${%s}", name);
        int index = temp.indexOf(findName);
        while (index >= 0) {
            if (index > 0) {
                if (temp.charAt(index - 1) == '\\')
                    return temp;
            }
            temp = String.format("%s%s%s", temp.substring(0, index), value, temp.substring(index + findName.length()));
            index = temp.indexOf(findName);
        }
        return temp;
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        boolean lastWasEscape = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (lastWasEscape) {
                sb.append(c);
                lastWasEscape = false;
            } else {
                if (c == '\\')
                    lastWasEscape = true;
                else {
                    sb.append(c);
                    lastWasEscape = false;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Like {@link replaceEnv(List<String>)}, but reads input from a file and writes
     * to a file.
     * 
     * @param   inFile  Path pointing to a template file, has to exist
     * @param   outFile Path pointing to an output file; will be overwritten if exists.
     */
    public static void replaceEnv(Path inFile, Path outFile) throws IOException {
        List<String> lines = Files.readAllLines(inFile);
        List<String> outLines = replaceEnv(lines);
        Files.write(outFile, outLines);
    }

    private static void printUsage() {
        System.out.println("Usage: java ReplaceEnv <infile> [<outfile>]");
    }

    private static Path resolveFile(String filePathString, boolean mustExist) throws IOException {
        Path filePath = FileSystems.getDefault().getPath(filePathString);
        if (mustExist && !filePath.toFile().exists())
            throw new IOException("File does not exist: '" + filePathString + "'.");
        return filePath;
    }

    /**
     * You can call this class as an executable; it takes one or two parameters,
     * inFile and outFile. If only inFile is present (the first parameter), the output
     * is written to stdout, otherwise it is written to outFile.
     * 
     * @param   args    args[0] is inFile, args[1] is (optionally) outFile.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        try {
            if (args.length == 1) {
                // Single input, output to stdout
                Path inPath = resolveFile(args[0], true);
                List<String> lines = Files.readAllLines(inPath);
                List<String> outLines = replaceEnv(lines);
                for (String line : outLines) {
                    System.out.println(line);
                }
            } else if (args.length == 2) {
                // Input and Output
                Path inPath = resolveFile(args[0], true);
                Path outPath = resolveFile(args[1], false);

                replaceEnv(inPath, outPath);
            }

            System.exit(0);
        } catch (Exception ex) {
            System.err.println("An error occurred: " + ex.getMessage());
            System.exit(1);
        }
    }
}
