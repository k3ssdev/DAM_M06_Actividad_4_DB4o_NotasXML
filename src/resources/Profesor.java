package resources;

public class Profesor {


     private Integer id;
     private String nombre;
     private String nomUser;
     private String password;


        public Profesor() {
        }

        public Profesor(Integer id, String nombre, String nomUser, String password) {
            this.id = id;
            this.nombre = nombre;
            this.nomUser = nomUser;
            this.password = password;
        }

        public Profesor(String string, String string2, String string3) {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getNomUser() {
            return nomUser;
        }

        public void setNomUser(String nomUser) {
            this.nomUser = nomUser;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "Profesor{" + "id=" + id + ", nombre=" + nombre + ", nomUser=" + nomUser + ", password=" + password + '}';
        }

    }
