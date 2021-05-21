package com.github.cilki.qcow4j;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class SnapshotTable {

	public static record Entry(
			// uint64_t l1_table_offset;
			long l1_table_offset,
			// uint32_t l1_size;
			int l1_size) {

		public static Entry read(FileChannel channel) throws IOException {

			var l1_table_offset = ByteBuffer.allocate(Long.BYTES);
			if (channel.read(l1_table_offset) != Long.BYTES)
				throw new IOException("Failed to read: l1_table_offset");

			var l1_size = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(l1_size) != Integer.BYTES)
				throw new IOException("Failed to read: l1_size");

			var id_str_size = ByteBuffer.allocate(Short.BYTES);
			if (channel.read(l1_size) != Short.BYTES)
				throw new IOException("Failed to read: id_str_size");

			return new Entry(//
					l1_table_offset.getLong(), //
					l1_size.getInt() //
			);
		}

		public void write(FileChannel channel) throws IOException {

			if (channel.write(ByteBuffer.allocate(Long.BYTES).putLong(l1_table_offset())) != Long.BYTES)
				throw new IOException("Failed to write: l1_table_offset");
		}
	}

	private final Qcow2 qcow2;

	private final List<Entry> entries;

	private final FileChannel channel;

	public SnapshotTable(Qcow2 qcow2) throws IOException {
		entries = new ArrayList<>();
		this.qcow2 = qcow2;

		channel = FileChannel.open(qcow2.file, READ, WRITE);
		channel.position(qcow2.header.snapshots_offset());

		for (int i = 0; i < qcow2.header.nb_snapshots(); i++) {
			entries.add(Entry.read(channel));
		}
	}

	public void take(String id, String name) throws IOException {
		try (var lock = channel.lock()) {

			// Copy current L1 table
			var l1_table = ByteBuffer.allocate(qcow2.header.l1_size());
			channel.read(l1_table, qcow2.header.l1_table_offset());

			// Write table to end
			long l1_table_offset = channel.size();
			channel.write(l1_table, l1_table_offset);

			// Write padding
			// TODO

			// Increment reference counts
			// TODO

			// Create snapshot entry and write it to the table
			var entry = new Entry(l1_table_offset, qcow2.header.l1_size());
			entries.add(entry);
			entry.write(channel);
		}
	}

	public void apply(String id) throws IOException {
		try (var lock = channel.lock()) {
			// TODO
		}
	}
}
