package com.github.cilki.qcow4j;

public class RefcountTable {

    private final Qcow2 qcow2;

    private final FileChannel channel;

    private final long[] entries;

    public RefcountTable(Qcow2 qcow2) {
        this.qcow2 = qcow2;

        this.channel = FileChannel.open(qcow2.file, READ, WRITE);
        this.channel.position(qcow2.header.refcount_table_offset());

        for (int i = 0; i < qcow2.header.refcount_table_clusters(); i++) {
			;
		}
    }

    public long lookup_refcount(long image_offset) {

        long refcount_block_entries = (qcow2.header.cluster_size() * 8) / refcount_bits;

        refcount_block_index = (image_offset / qcow2.header.cluster_size()) % refcount_block_entries;
        refcount_table_index = (image_offset / qcow2.header.cluster_size()) / refcount_block_entries;

        refcount_block = load_cluster(refcount_table[refcount_table_index]);
        return refcount_block[refcount_block_index];
    }

    public void increment_all() {
        
    }
}
