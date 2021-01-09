package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.programowaniezespolowe.planner.user.User;
import pl.programowaniezespolowe.planner.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(path = "/user")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/user/{id}")
    public Optional<User> getUser(@PathVariable String id) {
        int userId = Integer.parseInt(id);
        return userRepository.findById(userId);
    }

//    @PostMapping("/user")
//    public List<User> createUser(@RequestBody Map<String, String> body) {
//        String name = body.get("name");
//        String surname = body.get("surname");
//        int groupid =  Integer.valueOf(body.get("groupid"));
//        String email = body.get("email");
//        String password = body.get("password");
//        String permission = body.get("permission");
//        userRepository.save(new User(name, surname, groupid, email, password, permission));
//        return userRepository.findAll();
//    }

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        List<User> users = userRepository.findAll();

        for(User u : users) {
            if(u.getEmail().equals(email)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        String name = body.get("name");
        String surname = body.get("surname");
        int groupid =  Integer.valueOf(body.get("groupid"));
        String password = body.get("password");
        String permission = body.get("permission");
        boolean logged = false;

        userRepository.save(new User(name, surname, groupid, email, password, permission, logged));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/login")
    public int loginUser(@RequestBody User user) {
        List<User> users = userRepository.findAll();
        User us = null;

        for(User u : users) {
            if(u.getEmail().equals(user.getEmail()) && u.getPassword().equals(user.getPassword())) {
                us = u;
            }
        }

        if(us != null)
        {
            us.setLogged(true);
            userRepository.save(us);
            return us.getId();
        }
        else return -1;
    }

    @CrossOrigin
    @PostMapping("/logout/{id}")
    public void logoutUser(@PathVariable String id) {
        List<User> users = userRepository.findAll();
        User us = null;

        for(User u : users) {
            if(u.getId() == (Integer.valueOf(id))) {
                us = u;
            }
        }

        if(us != null)
        {
            us.setLogged(false);
            userRepository.save(us);
        }
    }



    @DeleteMapping("user/{id}")
    public List<User> deleteUser(@PathVariable String id) {
        int userId = Integer.parseInt(id);
        userRepository.deleteById(userId);
        return userRepository.findAll();
    }
}
