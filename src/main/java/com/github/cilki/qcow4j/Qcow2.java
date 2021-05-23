package com.github.cilki.qcow4j;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import com.github.cilki.qcow4j.QHeader.IllegalHeaderException;

public class Qcow2 {

	public final FileChannel channel;

	public final ClusterTable cluster_table;
	public final Path file;
	public final QHeader header;

	/**
	 * The current read/write position.
	 */
	private long position;

	public final RefcountTable refcount_table;

	public final SnapshotTable snapshot_table;

	public Qcow2(Path file) throws IOException, IllegalHeaderException {
		this.file = file;
		this.channel = FileChannel.open(file, READ, WRITE);
		this.header = QHeader.read(channel);
		System.out.println(this.header);
		this.snapshot_table = new SnapshotTable(this);
		this.refcount_table = new RefcountTable(this);
		this.cluster_table = new ClusterTable(this);
	}

	/**
	 * @return A new {@code InputStream} containing the virtual data.
	 */
	public QcowInputStream newInputStream() {
		return new QcowInputStream(this);
	}

	public long position() {
		return position;
	}

	public void position(long position) {
		this.position = position;
	}

	public int read(ByteBuffer data) throws IOException {
		int read = cluster_table.read(data, position);
		if (read != -1) {
			position += read;
		}
		return read;
	}

	public int read(ByteBuffer data, long vOffset) throws IOException {
		return cluster_table.read(data, vOffset);
	}

	public int write(ByteBuffer data) {
		int write = cluster_table.write(data, position);
		if (write != -1) {
			position += write;
		}
		return write;
	}

	public int write(ByteBuffer data, long vOffset) {
		return cluster_table.write(data, vOffset);
	}
}
