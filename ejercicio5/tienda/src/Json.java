import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json {
    private Json() {}
    public static String escribir(Object o) {
        StringBuilder sb = new StringBuilder();
        escribir(o, sb, 0);
        return sb.toString();
    }

    private static void escribir(Object o, StringBuilder sb, int nivel) {
        if (o == null) {
            sb.append("null");
        } else if (o instanceof String) {
            cadena((String) o, sb);
        } else if (o instanceof Number || o instanceof Boolean) {
            sb.append(o);
        } else if (o instanceof Map) {
            Map<?, ?> m = (Map<?, ?>) o;
            if (m.isEmpty()) { sb.append("{}"); return; }
            sb.append("{\n");
            int i = 0;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                sangria(sb, nivel + 1);
                cadena(String.valueOf(e.getKey()), sb);
                sb.append(": ");
                escribir(e.getValue(), sb, nivel + 1);
                if (++i < m.size()) sb.append(",");
                sb.append("\n");
            }
            sangria(sb, nivel);
            sb.append("}");
        } else if (o instanceof List) {
            List<?> l = (List<?>) o;
            if (l.isEmpty()) { sb.append("[]"); return; }
            sb.append("[\n");
            for (int i = 0; i < l.size(); i++) {
                sangria(sb, nivel + 1);
                escribir(l.get(i), sb, nivel + 1);
                if (i < l.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sangria(sb, nivel);
            sb.append("]");
        } else {
            throw new IllegalArgumentException("Tipo no soportado: " + o.getClass());
        }
    }

    private static void sangria(StringBuilder sb, int nivel) {
        for (int i = 0; i < nivel; i++) sb.append("  ");
    }

    private static void cadena(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }
    public static Object leer(String texto) {
        try {
            Parser p = new Parser(texto);
            Object v = p.valor();
            p.espacios();
            if (p.i != texto.length()) throw new IllegalArgumentException("Texto sobrante en el JSON");
            return v;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("JSON incompleto");
        }
    }

    private static final class Parser {
        private final String s;
        private int i = 0;

        Parser(String s) { this.s = s; }

        void espacios() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }

        Object valor() {
            espacios();
            char c = s.charAt(i);
            switch (c) {
                case '{': return objeto();
                case '[': return arreglo();
                case '"': return cadena();
                case 't': return literal("true", Boolean.TRUE);
                case 'f': return literal("false", Boolean.FALSE);
                case 'n': return literal("null", null);
                default:  return numero();
            }
        }

        Object literal(String texto, Object valor) {
            if (!s.startsWith(texto, i)) throw new IllegalArgumentException("JSON invalido en posicion " + i);
            i += texto.length();
            return valor;
        }

        Map<String, Object> objeto() {
            i++; // {
            Map<String, Object> m = new LinkedHashMap<>();
            espacios();
            if (s.charAt(i) == '}') { i++; return m; }
            while (true) {
                espacios();
                String clave = cadena();
                espacios();
                esperar(':');
                m.put(clave, valor());
                espacios();
                char c = s.charAt(i++);
                if (c == '}') break;
                if (c != ',') throw new IllegalArgumentException("Se esperaba ',' o '}' en posicion " + (i - 1));
            }
            return m;
        }

        List<Object> arreglo() {
            i++; // [
            List<Object> l = new ArrayList<>();
            espacios();
            if (s.charAt(i) == ']') { i++; return l; }
            while (true) {
                l.add(valor());
                espacios();
                char c = s.charAt(i++);
                if (c == ']') break;
                if (c != ',') throw new IllegalArgumentException("Se esperaba ',' o ']' en posicion " + (i - 1));
            }
            return l;
        }

        String cadena() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') break;
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'u':
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4;
                            break;
                        default: sb.append(e); // \" \\ \/
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Double numero() {
            int inicio = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
            if (inicio == i) throw new IllegalArgumentException("JSON invalido en posicion " + i);
            return Double.valueOf(s.substring(inicio, i));
        }

        void esperar(char c) {
            if (s.charAt(i) != c) throw new IllegalArgumentException("Se esperaba '" + c + "' en posicion " + i);
            i++;
        }
    }
}
