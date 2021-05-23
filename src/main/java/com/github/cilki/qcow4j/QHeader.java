package com.github.cilki.qcow4j;

import java.io.IOException;
import java.io.RandomAccessFile;

public record QHeader(
		// uint32_t magic;
		int magic,
		// uint32_t version;
		int version,
		// uint64_t backing_file_offset;
		long backing_file_offset,
		// uint32_t backing_file_size;
		int backing_file_size,
		// uint32_t cluster_bits;
		int cluster_bits,
		// uint64_t size;
		long size,
		// uint32_t crypt_method;
		int crypt_method,
		// uint32_t l1_size;
		int l1_size,
		// uint64_t l1_table_offset;
		long l1_table_offset,
		// uint64_t refcount_table_offset;
		long refcount_table_offset,
		// uint32_t refcount_table_clusters;
		int refcount_table_clusters,
		// uint32_t nb_snapshots;
		int nb_snapshots,
		// uint64_t snapshots_offset;
		long snapshots_offset) {

	public static QHeader read(FileChannel channel) throws IOException, IllegalHeaderException {

		var magic = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(magic) != Integer.BYTES)
			throw new IOException("Failed to read: magic");

		var version = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(version) != Integer.BYTES)
			throw new IOException("Failed to read: version");

		var backing_file_offset = ByteBuffer.allocate(Long.BYTES);
		if (channel.read(backing_file_offset) != Long.BYTES)
			throw new IOException("Failed to read: backing_file_offset");

		var backing_file_size = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(backing_file_size) != Integer.BYTES)
			throw new IOException("Failed to read: backing_file_size");

		var backing_file_size = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(backing_file_size) != Integer.BYTES)
			throw new IOException("Failed to read: backing_file_size");

		var cluster_bits = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(cluster_bits) != Integer.BYTES)
			throw new IOException("Failed to read: cluster_bits");

		var size = ByteBuffer.allocate(Long.BYTES);
		if (channel.read(size) != Long.BYTES)
			throw new IOException("Failed to read: size");

		var crypt_method = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(crypt_method) != Integer.BYTES)
			throw new IOException("Failed to read: crypt_method");

		var l1_size = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(l1_size) != Integer.BYTES)
			throw new IOException("Failed to read: l1_size");

		var l1_table_offset = ByteBuffer.allocate(Long.BYTES);
		if (channel.read(l1_table_offset) != Long.BYTES)
			throw new IOException("Failed to read: l1_table_offset");

		var refcount_table_offset = ByteBuffer.allocate(Long.BYTES);
		if (channel.read(refcount_table_offset) != Long.BYTES)
			throw new IOException("Failed to read: refcount_table_offset");

		var refcount_table_clusters = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(refcount_table_clusters) != Integer.BYTES)
			throw new IOException("Failed to read: refcount_table_clusters");

		var nb_snapshots = ByteBuffer.allocate(Integer.BYTES);
		if (channel.read(nb_snapshots) != Integer.BYTES)
			throw new IOException("Failed to read: nb_snapshots");

		var snapshots_offset = ByteBuffer.allocate(Long.BYTES);
		if (channel.read(snapshots_offset) != Long.BYTES)
			throw new IOException("Failed to read: snapshots_offset");

		// Validate magic
		if (magic.getInt() != 0x514649fb) {
			throw new IllegalHeaderException("magic", magic.getInt());
		}

		// Validate version
		if (version.getInt() != 2 && version.getInt() != 3) {
			throw new IllegalHeaderException("version", version.getInt());
		}

		return new QHeader( //
				magic.getInt(), //
				version.getInt(), //
				backing_file_offset.getLong(), //
				backing_file_size.getInt(), //
				cluster_bits.getInt(), //
				size.getLong(), //
				crypt_method.getInt(), //
				l1_size.getInt(), //
				l1_table_offset.getLong(), //
				refcount_table_offset.getLong(), //
				refcount_table_clusters.getInt(), //
				nb_snapshots.getInt(), //
				snapshots_offset.getLong() //
		);
	}

	public void write(FileChannel channel) throws IOException {

	}

	public static class IllegalHeaderException extends Exception {
		public IllegalHeaderException(String field, Number value) {
			super("Field '" + field + "' was invalid: " + value);
		}
	}

	public int cluster_size() {
		return 1 << cluster_bits();
	}

	public int l2_entries() {
		return cluster_size() / Long.BYTES;
	}

	public int header_length() {
		return 72;
	}
}
