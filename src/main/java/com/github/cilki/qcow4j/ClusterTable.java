package com.github.cilki.qcow4j;

public class ClusterTable {

    private final Qcow2 qcow2;

    private final FileChannel channel;

    private final long[] l1_table;

    public ClusterTable(Qcow2 qcow2) {
        this.qcow2 = qcow2;

        this.channel = FileChannel.open(qcow2.file, READ, WRITE);
        this.channel.position(qcow2.header.l1_table_offset());

        var table_buffer = ByteBuffer.allocate(qcow2.header.l1_size);
        l1_table = table_buffer.longArray();
    }

    public byte[] read(long cluster_index) throws IOException {

		var cluster = ByteBuffer.allocate(header.cluster_size());
        channel.read(cluster, cluster_index * header.cluster_size());

		return cluster.array();
	}

}
