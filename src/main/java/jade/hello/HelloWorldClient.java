package jade.hello;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jade.grpc.hello.GreeterGrpc;
import jade.grpc.hello.Helloworld;

import java.util.concurrent.TimeUnit;

public class HelloWorldClient {

    private final ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(String hostname, int port) {
        channel = ManagedChannelBuilder.forAddress(hostname, port)
                .usePlaintext(true)
                .build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        try {
            Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setName(name).build();
            Helloworld.HelloReply response = blockingStub.sayHello(request);
            System.out.println(response.getMessage());
        }
        catch (RuntimeException re) {
            re.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        HelloWorldClient client = new HelloWorldClient("localhost", 42420);
        String name = args.length > 0 ? args[0] : "unknown";
        try {
            client.greet(name);
        } finally {
            client.shutdown();
        }
    }
}
