import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class InteractiveChatSystem {

    static class ChatUser extends Thread {
        private final LinkedBlockingQueue<String> inbox = new LinkedBlockingQueue<>();
        private volatile boolean running = true;
        private volatile boolean paused = false;
        private final Object pauseLock = new Object();

        ChatUser(String name) {
            super(name);
        }

        // Safe stop method
        public void stopThread() {
            running = false;
            this.interrupt();
            System.out.println(getName() + " stopping...");
        }

        // Pause and resume
        public void pauseThread() {
            paused = true;
            System.out.println(getName() + " paused.");
        }

        public void resumeThread() {
            synchronized (pauseLock) {
                paused = false;
                pauseLock.notifyAll();
            }
            System.out.println(getName() + " resumed.");
        }

        // Send message to another user
        public void sendMessage(ChatUser to, String msg) {
            to.inbox.offer(getName() + ": " + msg);
            System.out.println(getName() + " → " + to.getName() + ": " + msg);
        }

        @Override
        public void run() {
            System.out.println(getName() + " started (Priority " + getPriority() + ")");
            try {
                while (running) {
                    if (paused) {
                        synchronized (pauseLock) {
                            while (paused && running) pauseLock.wait();
                        }
                    }
                    String msg = inbox.poll(300, java.util.concurrent.TimeUnit.MILLISECONDS);
                    if (msg != null) {
                        System.out.println("📨 " + getName() + " received: " + msg);
                        Thread.sleep(200); // simulate processing
                    }
                }
            } catch (InterruptedException e) {
                // Exit gracefully
            }
            System.out.println(getName() + " exited.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ChatUser alice = new ChatUser("Alice");
        ChatUser bob = new ChatUser("Bob");
        alice.setPriority(Thread.NORM_PRIORITY + 1);
        bob.setPriority(Thread.NORM_PRIORITY);

        alice.start();
        bob.start();

        System.out.println("\n=== Multi-threaded Chat Simulation ===");
        System.out.println("Commands:");
        System.out.println("1. send <sender> <receiver> <message>");
        System.out.println("2. pause <user>");
        System.out.println("3. resume <user>");
        System.out.println("4. stop <user>");
        System.out.println("5. state");
        System.out.println("6. exit");
        System.out.println("--------------------------------------");

        boolean exit = false;
        while (!exit) {
            System.out.print("> ");
            String input = sc.nextLine().trim();
            String[] parts = input.split(" ", 4);

            if (parts.length == 0) continue;
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "send":
                    if (parts.length < 4) {
                        System.out.println("Usage: send <sender> <receiver> <message>");
                        break;
                    }
                    ChatUser sender = getUser(parts[1], alice, bob);
                    ChatUser receiver = getUser(parts[2], alice, bob);
                    if (sender != null && receiver != null)
                        sender.sendMessage(receiver, parts[3]);
                    else
                        System.out.println("Invalid user name.");
                    break;

                case "pause":
                    getUser(parts.length > 1 ? parts[1] : "", alice, bob).pauseThread();
                    break;

                case "resume":
                    getUser(parts.length > 1 ? parts[1] : "", alice, bob).resumeThread();
                    break;

                case "stop":
                    getUser(parts.length > 1 ? parts[1] : "", alice, bob).stopThread();
                    break;

                case "state":
                    System.out.println("Alice alive: " + alice.isAlive());
                    System.out.println("Bob alive:   " + bob.isAlive());
                    break;

                case "exit":
                    alice.stopThread();
                    bob.stopThread();
                    try {
                        alice.join();
                        bob.join();
                    } catch (InterruptedException e) { }
                    exit = true;
                    System.out.println("Chat ended.");
                    break;

                default:
                    System.out.println("Unknown command.");
            }
        }

        sc.close();
    }

    private static ChatUser getUser(String name, ChatUser a, ChatUser b) {
        if (name.equalsIgnoreCase("alice")) return a;
        if (name.equalsIgnoreCase("bob")) return b;
        return null;
    }
}
