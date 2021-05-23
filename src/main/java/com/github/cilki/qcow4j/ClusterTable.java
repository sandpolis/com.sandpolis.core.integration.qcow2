package com.github.cilki.qcow4j;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ClusterTable {

	private final Qcow2 qcow2;

	private final FileChannel channel;

	private final ByteBuffer l1_table;

	public ClusterTable(Qcow2 qcow2) throws IOException {
		this.qcow2 = qcow2;

		this.channel = FileChannel.open(qcow2.file, READ, WRITE);
		this.channel.position(qcow2.header.l1_table_offset());

		l1_table = ByteBuffer.allocateDirect(qcow2.header.l1_size());
		if (channel.read(l1_table) != qcow2.header.l1_size()) {
			throw new IOException();
		}
	}

	public byte[] read(long cluster_index) throws IOException {

		var cluster = ByteBuffer.allocateDirect(qcow2.header.cluster_size());
		channel.read(cluster, cluster_index * qcow2.header.cluster_size());

		return cluster.array();
	}

}
