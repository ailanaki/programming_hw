package info.kgeorgiy.ja.yakupova.walk;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;


public class Walk {
    public static void fileWorker(Path inputPath, String outputName) {
        try {
            final Path outputPath = Path.of(outputName);
            if (!Files.exists(outputPath)) {
                // NOTE: no need for toAbsolutePath
                Path parent = outputPath.toAbsolutePath().getParent();
                // NOTE: no need for the first check (isDirectory)
                if (!(Files.exists(parent) && Files.isDirectory(parent)) && !createOutputPath(parent)) {
                    return;
                }
            } else {
                if (!Files.isRegularFile(outputPath)) {
                    System.err.println("Output is not a regular file");
                    return;
                }
            }
            try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
                // NOTE: try-with-resource + detailed exceptions
                // output_name = /home/vasya/work/out; /.../work — does not exist
                // NOTE: handle non-existing parent directories
                try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                    String line;
                    long result;
                    while ((line = reader.readLine()) != null) {
                        result = hash(line);
                        writer.write(String.format("%016x %s", result, line));
                        writer.newLine();
                    }
                } catch (IOException io) {
                    System.err.println("Output file exception:" + io.getMessage());
                }
            } catch (IOException io) {
                System.err.println("Input file exception:" + io.getMessage());
            }

        } catch (InvalidPathException io) {
            System.err.println("Invalid path: " + outputName);
        }

    }

    public static boolean createOutputPath(Path parentPath) {
        try {
            Files.createDirectories(parentPath);
        } catch (FileAlreadyExistsException io) {
            System.err.println("Output path directory is already existing file");
            return false;
        } catch (IOException io) {
            System.err.println("Can't create directory because of: " + io);
            return false;
        }
        return true;
    }

    public static long hash(String input) {
        long h = 0, high;
        try (InputStream inputStream = new FileInputStream(input)) {
            // NOTE: close streams
            // NOTE: read blockwise
            byte[] buffer = new byte[2048];
            int b;
            while ((b = inputStream.read(buffer)) > -1) {
                for (int i = 0; i < b; i++) {
                    h = (h << 8) + (buffer[i] & 0xff);
                    if ((high = h & 0xff00_0000_0000_0000L) != 0) {
                        h ^= high >> 48;
                    }
                    h &= ~high;
                }
            }
        } catch (IOException e) {
            h = 0;
        }
        return h;
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Invalid input(input is null)");
            return;
        }
        if (args.length < 2) {
            System.err.println("Need two files to work, got: " + args.length);
            return;
        }
        if (args[0] == null || args[1] == null) {
            System.err.println("Some files-name are null ");
            return;
        }
        String inputName = args[0];
        try {
            final Path inputPath = Path.of(inputName);
            // NOTE: separate into two cases
            if (!Files.exists(inputPath)) {
                System.err.println("Input file not exists");
                return;
            }
            if (!Files.isRegularFile(inputPath)) {
                System.err.println("Input is not a regular file");
                return;
            }
            String outputName = args[1];
            fileWorker(inputPath, outputName);
        } catch (InvalidPathException io) {
            System.err.println("Invalid path: " + inputName);
        }
    }
}
