package edu.gatech.mbsec.adapter.magicdraw.writer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openjena.riot.Lang;

/**
 * A remote HTTP {@link Model model} {@link ModelWriter writer}.
 * @author rherrera
 */
public class HttpModelWriter implements ModelWriter {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(HttpModelWriter.class.getName());
    /**
     * Internal writer class.
     */
    private static class Writer implements Runnable {
        private final OutputStream output;
        private final Model model;
        private final Lang language;
        /**
         * Constructs an instance specifying the output stream, the input model
         * and the desired serialization language.
         * @param output the output stream.
         * @param model the input model.
         * @param language the serialization language.
         */
        public Writer(OutputStream output, Model model, Lang language) {
            this.language = language;
            this.output = output;
            this.model = model;
        }

        @Override
        public void run() {
            RDFWriter writer = model.getWriter(language.getName());
            try(OutputStream clone = output) {
                writer.write(model, output, (String)null);
                clone.flush();
            } catch(IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    /**
     * The target URL.
     */
    private final URL target;
    /**
     * The hint for the final graph name.
     */
    private final String slug;
    /**
     * Constructs an instance specifying the target URL.
     * @param url the target host URL.
     * @param slug the hint for the final graph name.
     * @throws NullPointerException if the slug is null.
     * @throws MalformedURLException if the URL is not well formed.
     */
    public HttpModelWriter(String url, String slug)
            throws MalformedURLException {
        this.slug = Objects.requireNonNull(slug, "slug cannot be null");
        this.target = new URL(url);
    }
    /**
     * Gets the target URL.
     * @return the target URL.
     */
    public URL getTarget() {
        return target;
    }
    /**
     * Gets the hint for the final graph name.
     * @return the hint for the final graph name.
     */
    public String getSlug() {
        return slug;
    }
    /**
     * Handles the http response.
     * @param response the http response.
     * @throws IOException if some I/O exception occurs.
     */
    private void handleResponse(CloseableHttpResponse response)
            throws IOException {
        String data;
        int code = response.getStatusLine().getStatusCode();
        String text = EnglishReasonPhraseCatalog.INSTANCE.getReason(code, null);
        switch(code) {
            case HttpStatus.SC_CREATED:
                data = response.getFirstHeader("Location").getValue();
                break;
            case HttpStatus.SC_NOT_FOUND:
                data = target.toString();
                break;
            default:
                data = EntityUtils.toString(response.getEntity());
        }
        LOG.log(Level.INFO, "{0}: {1}", new Object[]{text, data});
    }

    @Override
    public void write(Model model, Lang language) throws IOException {
        PipedOutputStream buffer = new PipedOutputStream();
        PipedInputStream serialization = new PipedInputStream(buffer);
        Thread writer = new Thread(new Writer(buffer, model, language));
        HttpPost post = new HttpPost(target.toString());
        InputStreamEntity entity = new InputStreamEntity(serialization);
        entity.setContentType(language.getContentType());
        entity.setContentEncoding("UTF-8");
        entity.setChunked(true);
        post.setEntity(entity);
        post.setHeader("Slug", slug);
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            writer.start();
            try (CloseableHttpResponse response = client.execute(post)) {
                handleResponse(response);
            }
        }
    }

}