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

	public static QHeader parse(RandomAccessFile in) throws IOException, IllegalHeaderException {

		in.seek(0);
		var header = new QHeader( //
				in.readInt(), // magic
				in.readInt(), // version
				in.readLong(), // backing_file_offset
				in.readInt(), // backing_file_size
				in.readInt(), // cluster_bits
				in.readLong(), // size
				in.readInt(), // crypt_method
				in.readInt(), // l1_size
				in.readLong(), // l1_table_offset
				in.readLong(), // refcount_table_offset
				in.readInt(), // refcount_table_clusters
				in.readInt(), // nb_snapshots
				in.readLong() // snapshots_offset
		);

		if (header.magic() != 0x514649fb) {
			throw new IllegalHeaderException("magic", header.magic());
		}

		if (header.version() != 2 && header.version() != 3) {
			throw new IllegalHeaderException("version", header.version());
		}

		System.out.println("Parsed header: " + header);

		return header;

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
