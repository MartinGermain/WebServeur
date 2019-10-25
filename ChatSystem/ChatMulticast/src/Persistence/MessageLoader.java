package Persistence;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessageLoader {

    private final Gson gson;


    public MessageLoader() {
        gson = new Gson();
    }

    /**
     * Saves the chat in a Json file at the provided path
     *
     * @param messages The List that contains the chat 
     * @param filePath The path of the file to be read.
     * 
     */
    public void saveMessages(final List messages,
                              final File filePath) throws IOException {
        if (messages == null) {
            throw new IllegalArgumentException("projectData is null");
        }
        if (filePath == null) {
            throw new IllegalArgumentException("filePath is null");
        }
        String jsonFile = gson.toJson(messages, List.class);
        writeFile(jsonFile, filePath);
    }

    /**
     * Loads the chat from a Json file located at the provided path
     *
     * @param filePath The path of the file to be read.
     * @return the List that contains the chat
     */
    public List loadMessages(final File filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath is null");
        }
        final String loadedJSON = loadFile(filePath);
        try {
            final List messages  = gson.fromJson(loadedJSON, List.class);
            return messages;
        } catch (final JsonSyntaxException e) {
            throw new IOException("File format invalid");
        }
    }
    /**
     * Loads a whole plaintext file.
     *
     * @param filePath The path of the file to be read.
     * @return The file content as a string.
     * @throws IOException if the file could not be read
     */
    private String loadFile(final File filePath) throws IOException {
        return Files.asCharSource(filePath, Charsets.UTF_8).read();
    }

    /**
     * Overwrites the given file with the given string or creates a new file containing the string.
     *
     * @param content  The new file content.
     * @param filePath The path of the file to be written.
     * @throws IOException if the file could not be written
     */
    private void writeFile(final String content, final File filePath)
            throws IOException {
        Files.asCharSink(filePath, Charsets.UTF_8).write(content);
    }

}
