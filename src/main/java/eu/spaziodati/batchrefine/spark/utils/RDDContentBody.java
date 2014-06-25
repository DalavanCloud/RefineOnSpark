package eu.spaziodati.batchrefine.spark.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

/**
 * This class deals with the materialisation of RDD,on the worker node. It is an {@code Iterator<String>}
 * which has to be passed to  HTTPClient as  {@link AbstractContentBody}.
 * 
 * Inteligence of adding a header to chunks is located here:
 * check if a chunk has a header
 * - if not, add it.
 * 
 * 
 * @param Iterator<String> rdd
 * @param String header
 * @author andrey
 */



public class RDDContentBody extends AbstractContentBody {
	private final Iterator<String> rdd;
	private final String header;

	public RDDContentBody(Iterator<String> rdd, String header) {
		super(ContentType.DEFAULT_BINARY);
		this.rdd = rdd;
		this.header = header;
	}

	@Override
	public String getFilename() {
		return "tmp.csv";
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {

		if (rdd.hasNext()) {
			String firstLine = rdd.next();
			if (firstLine.equals(header))
				out.write((header + "\n").getBytes());
			else
				out.write((header + "\n" + firstLine + "\n").getBytes());
		}
		while (rdd.hasNext()) {
			out.write((rdd.next() + "\n").getBytes());
		}
		out.write(-1);
	}

	public String getTransferEncoding() {
		return MIME.ENC_BINARY;
	}

	@Override
	public long getContentLength() {
		return -1;
	}

}
