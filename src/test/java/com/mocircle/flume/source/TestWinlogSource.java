package com.mocircle.flume.source;

import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestWinlogSource {

	WinlogSource source;
	MemoryChannel channel;

	@Before
	public void setUp() {
		source = new WinlogSource();
		channel = new MemoryChannel();

		Configurables.configure(channel, new Context());

		List<Channel> channels = new ArrayList<Channel>();
		channels.add(channel);

		ChannelSelector rcs = new ReplicatingChannelSelector();
		rcs.setChannels(channels);

		source.setChannelProcessor(new ChannelProcessor(rcs));
	}

	@After
	public void tearDown() {

	}

	@Test
	public void test() {
		Context context = new Context();
		context.put("remote", "false");
		context.put("eventChannels", "application, security,system");
		context.put("recordStatusFile", "d:\\record.status");
		context.put("batchSize", "10");

		Configurables.configure(source, context);
		source.start();
		try {
			while (true) {
				Thread.sleep(1000);
				source.process();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
