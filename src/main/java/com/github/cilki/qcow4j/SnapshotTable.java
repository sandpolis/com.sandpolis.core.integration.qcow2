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
			int l1_size,
			// uint16_t id_str_size;
			short id_str_size,
			// uint16_t name_size;
			short name_size,
			// uint32_t date_sec;
			int date_sec,
			// uint32_t date_nsec;
			int date_nsec,
			// uint64_t vm_clock_nsec;
			long vm_clock_nsec,
			// uint32_t vm_state_size;
			int vm_state_size,
			// uint32_t extra_data_size;
			int extra_data_size,
			// *uint8_t extra_data;
			byte[] extra_data,
			// *uint8_t id_str;
			String id_str,
			// *uint8_t name;
			String name) {

		public static Entry read(FileChannel channel) throws IOException {

			var l1_table_offset = ByteBuffer.allocate(Long.BYTES);
			if (channel.read(l1_table_offset) != Long.BYTES)
				throw new IOException("Failed to read: l1_table_offset");

			var l1_size = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(l1_size) != Integer.BYTES)
				throw new IOException("Failed to read: l1_size");

			var id_str_size = ByteBuffer.allocate(Short.BYTES);
			if (channel.read(id_str_size) != Short.BYTES)
				throw new IOException("Failed to read: id_str_size");

			var name_size = ByteBuffer.allocate(Short.BYTES);
			if (channel.read(name_size) != Short.BYTES)
				throw new IOException("Failed to read: name_size");

			var date_sec = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(date_sec) != Integer.BYTES)
				throw new IOException("Failed to read: date_sec");

			var date_nsec = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(date_nsec) != Integer.BYTES)
				throw new IOException("Failed to read: date_nsec");

			var vm_clock_nsec = ByteBuffer.allocate(Long.BYTES);
			if (channel.read(vm_clock_nsec) != Long.BYTES)
				throw new IOException("Failed to read: vm_clock_nsec");

			var vm_state_size = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(vm_state_size) != Integer.BYTES)
				throw new IOException("Failed to read: vm_state_size");

			var extra_data_size = ByteBuffer.allocate(Integer.BYTES);
			if (channel.read(extra_data_size) != Integer.BYTES)
				throw new IOException("Failed to read: extra_data_size");

			var extra_data = ByteBuffer.allocate(extra_data_size.getInt());
			if (channel.read(extra_data) != extra_data_size.getInt())
				throw new IOException("Failed to read: extra_data");

			var id_str = ByteBuffer.allocate(id_str_size.getShort());
			if (channel.read(id_str) != id_str_size.getShort())
				throw new IOException("Failed to read: id_str");

			var name = ByteBuffer.allocate(name_size.getShort());
			if (channel.read(name) != name_size.getShort())
				throw new IOException("Failed to read: name");

			return new Entry( //
					l1_table_offset.getLong(), //
					l1_size.getInt(), //
					id_str_size.getShort(), //
					name_size.getShort(), //
					date_sec.getInt(), //
					date_nsec.getInt(), //
					vm_clock_nsec.getLong(), //
					vm_state_size.getInt(), //
					extra_data_size.getInt(), //
					extra_data.array(), //
					new String(id_str.array()), //
					new String(name.array()) //
			);
		}

		public void write(FileChannel channel) throws IOException {

			if (channel.write(ByteBuffer.allocate(Long.BYTES).putLong(l1_table_offset())) != Long.BYTES)
				throw new IOException("Failed to write: l1_table_offset");

			if (channel.write(ByteBuffer.allocate(Integer.BYTES).putInt(l1_size())) != Integer.BYTES)
				throw new IOException("Failed to write: l1_size");
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
			channel.write(ByteBuffer.allocate(0 /* TODO */), l1_table_offset + l1_table.capacity());

			// Increment reference counts
			qcow2.refcount_table.increment_all();

			// Create snapshot entry and write it to the table
			var entry = new Entry(l1_table_offset, qcow2.header.l1_size(), (short) id.length(), (short) name.length(),
					0, 0, 0, 0, 0, null, id, name);
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
