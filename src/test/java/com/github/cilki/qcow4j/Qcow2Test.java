package com.github.cilki.qcow4j;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Qcow2Test {

	@Test
	void test() throws Exception {
		var qcow2 = new Qcow2(Paths.get("src/test/resources/empty_small.qcow2"));

		assertEquals(3, qcow2.header.version());
		assertEquals(0, qcow2.header.nb_snapshots());
		assertEquals(16, qcow2.header.cluster_bits());
		assertEquals(1024, qcow2.header.size());
	}
}
