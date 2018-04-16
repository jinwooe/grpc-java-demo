package jade.hello;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import jade.grpc.hello.Helloworld.HelloRequest;
import jade.grpc.hello.Helloworld.HelloReply;
import jade.grpc.hello.GreeterGrpc;

import java.io.IOException;

public class HelloWorldServer {

    private final int port;
    private final Server server;

    public HelloWorldServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HelloWorldServer.this.stop();
        }));
    }

    private void stop() {
        if(server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws Exception {
        final HelloWorldServer helloWorldServer = new HelloWorldServer(42420);
        helloWorldServer.blockUntilShutdown();
    }


    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply response = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
