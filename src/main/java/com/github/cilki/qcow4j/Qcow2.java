package com.github.cilki.qcow4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.github.cilki.qcow4j.QHeader.IllegalHeaderException;

public class Qcow2 {

	public final QHeader header;

	private final RandomAccessFile image;
	
	public final Path file;

	private long[] l1_table;

	public Qcow2(Path file) throws IOException, IllegalHeaderException {
		this.file = file;
		image = new RandomAccessFile(file.toFile(), "rw");
		header = QHeader.parse(image);
	}

	public void write(byte[] data) {

	}

	public byte[] read(long address, int length) throws IOException {

		int l2_index = (int) (address / header.cluster_size()) % header.l2_entries();
		int l1_index = (int) (address / header.cluster_size()) / header.l2_entries();

		if (l1_table[l1_index] == 0) {
			return new byte[header.cluster_size()];
		}

		byte[] l2_table = cluster_read(l1_table[l1_index]);

		if (l2_table[l2_index] == 0) {
			return new byte[header.cluster_size()];
		}

		long cluster_offset = l2_table[l2_index];
		return null;
	}
	
	private void snapshot_create() {
		
	}

}
