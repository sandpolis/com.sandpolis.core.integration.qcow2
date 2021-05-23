package com.github.cilki.qcow4j;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import com.github.cilki.qcow4j.QHeader.IllegalHeaderException;

public class Qcow2 {

	public final QHeader header;

	public final SnapshotTable snapshot_table;
	public final RefcountTable refcount_table;
	public final ClusterTable cluster_table;

	public final Path file;

	public final FileChannel channel;

	private long[] l1_table;

	public Qcow2(Path file) throws IOException, IllegalHeaderException {
		this.file = file;
		this.channel = FileChannel.open(file, READ, WRITE);
		this.header = QHeader.read(channel);
		this.snapshot_table = new SnapshotTable(this);
		this.refcount_table = new RefcountTable(this);
		this.cluster_table = new ClusterTable(this);
	}

	public void write(byte[] data) {

	}

	public byte[] read(long address, int length) throws IOException {

		int l2_index = (int) (address / header.cluster_size()) % header.l2_entries();
		int l1_index = (int) (address / header.cluster_size()) / header.l2_entries();

		if (l1_table[l1_index] == 0) {
			return new byte[header.cluster_size()];
		}

//		byte[] l2_table = cluster_read(l1_table[l1_index]);
//
//		if (l2_table[l2_index] == 0) {
//			return new byte[header.cluster_size()];
//		}
//
//		long cluster_offset = l2_table[l2_index];
		return null;
	}

}
