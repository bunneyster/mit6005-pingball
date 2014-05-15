package pb.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import pb.net.SocketFetcher;
import pb.proto.Message;

/** Pushes the {@link Messages} coming out of a {@link Socket} onto a queue. */
public class ClientFetcher extends SocketFetcher<Message> {
	public ClientFetcher(BlockingQueue<Message> recvQueue, Socket socket,
			BlockingQueue<Message> pusherQueue) throws IOException {
		super(recvQueue, socket, pusherQueue);
	}
	
	@Override
	protected Message convertMessage(Message message) {
		return message;
	}

}