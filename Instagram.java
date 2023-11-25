import java.io.*;
import java.util.ArrayList;
import java.util.List;

class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username + "," + email;
    }
}

class Post {
    private User author;
    private String caption;
    private String imageUrl;

    public Post(User author, String caption, String imageUrl) {
        this.author = author;
        this.caption = caption;
        this.imageUrl = imageUrl;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return author.getUsername() + "," + caption + "," + imageUrl;
    }
}

class Comment {
    private User author;
    private String text;

    public Comment(User author, String text) {
        this.author = author;
        this.text = text;
    }

    @Override
    public String toString() {
        return author.getUsername() + "," + text;
    }
}

public class Instagram {
    private static List<User> users = new ArrayList<>();
    private static List<Post> posts = new ArrayList<>();
    private static List<Comment> comments = new ArrayList<>();

    private static final String USERS_FILE = "users.txt";
    private static final String POSTS_FILE = "posts.txt";
    private static final String COMMENTS_FILE = "comments.txt";

    public static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.add(new User(parts[0], parts[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPosts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(POSTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                User author = findUserByUsername(parts[0]);
                if (author != null) {
                    posts.add(new Post(author, parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadComments() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COMMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                User author = findUserByUsername(parts[0]);
                if (author != null) {
                    comments.add(new Comment(author, parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static User createUser(String username, String email) {
        if (isValidEmail(email)) {
            User newUser = new User(username, email);
            users.add(newUser);
            saveUsers();
            return newUser;
        } else {
            throw new IllegalArgumentException("Invalid email address.");
        }
    }

    public static boolean isValidEmail(String email) {
        // Implement email validation logic here (simplified for demonstration).
        return email.contains("@");
    }

    public static void createPost(User author, String caption, String imageUrl) {
        if (author != null) {
            Post post = new Post(author, caption, imageUrl);
            posts.add(post);
            savePosts();
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public static void createComment(User author, Post post, String text) {
        if (author != null && post != null) {
            Comment comment = new Comment(author, text);
            comments.add(comment);
            saveComments();
        } else {
            throw new IllegalArgumentException("Invalid user or post.");
        }
    }

    public static void deletePost(User author, Post post) {
        if (author != null && post != null) {
            if (posts.remove(post)) {
                savePosts();
            }
        } else {
            throw new IllegalArgumentException("Invalid user or post.");
        }
    }

    public static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePosts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSTS_FILE))) {
            for (Post post : posts) {
                writer.write(post.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveComments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMMENTS_FILE))) {
            for (Comment comment : comments) {
                writer.write(comment.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadUsers();
        loadPosts();
        loadComments();

        User user1 = createUser("user123", "user123@example.com");
        User user2 = createUser("johndoe", "johndoe@example.com");

        createPost(user1, "Hello, Instagram!", "image1.jpg");
        createPost(user2, "Fun at the beach", "image2.jpg");
        createPost(user1, "New recipe I tried", "image3.jpg");

        // user1.viewTimeline();

        createComment(user2, posts.get(0), "Great photo!");
        createComment(user1, posts.get(0), "Yum, that looks delicious!");

        deletePost(user1, posts.get(0));

        // Exception handling use-cases
        try {
            createUser("invaliduser", "invalid-email");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            createComment(user1, null, "Invalid comment.");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // Save the updated data to files
        saveUsers();
        savePosts();
        saveComments();
    }
}
