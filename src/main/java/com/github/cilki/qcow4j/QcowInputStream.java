package com.github.cilki.qcow4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class QcowInputStream extends InputStream {

	private final Qcow2 qcow2;

	private final ByteBuffer buffer;

	private long offset;

	QcowInputStream(Qcow2 qcow2) {
		this.qcow2 = qcow2;
		buffer = ByteBuffer.allocateDirect(qcow2.header.cluster_size());
		buffer.limit(0);
	}

	@Override
	public int read() throws IOException {
		if (!buffer.hasRemaining()) {
			int read = qcow2.read(buffer.clear(), offset);
			if (read == -1) {
				return -1;
			}
			buffer.limit(read);
			offset += read;
		}
		return buffer.get();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return super.read(b, off, len);
	}

}
