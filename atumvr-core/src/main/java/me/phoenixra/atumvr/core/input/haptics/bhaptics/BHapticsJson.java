package me.phoenixra.atumvr.core.input.haptics.bhaptics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


final class BHapticsJson {

    private BHapticsJson() {
    }

    // -------- READ --------

    static @Nullable Object parse(@NotNull String text) {
        Parser parser = new Parser(text);
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isDone()) {
            throw new IllegalArgumentException(
                    "Trailing content at " + parser.pos + " in json: " + preview(text)
            );
        }
        return value;
    }

    private static final class Parser {
        private final String text;
        private int pos;

        private Parser(String text) {
            this.text = text;
        }

        private boolean isDone() {
            return pos >= text.length();
        }

        private void skipWhitespace() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            if (isDone()) {
                throw new IllegalArgumentException("Unexpected end of json: " + preview(text));
            }
            return text.charAt(pos);
        }

        private void expect(char c) {
            if (peek() != c) {
                throw new IllegalArgumentException(
                        "Expected '" + c + "' at " + pos + " in json: " + preview(text)
                );
            }
            pos++;
        }

        private Object parseValue() {
            skipWhitespace();
            char c = peek();
            switch (c) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                    expectWord("true");
                    return Boolean.TRUE;
                case 'f':
                    expectWord("false");
                    return Boolean.FALSE;
                case 'n':
                    expectWord("null");
                    return null;
                default:
                    return parseNumber();
            }
        }

        private Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> map = new LinkedHashMap<>();
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                map.put(key, parseValue());
                skipWhitespace();
                char c = peek();
                pos++;
                if (c == '}') {
                    return map;
                }
                if (c != ',') {
                    throw new IllegalArgumentException(
                            "Expected ',' or '}' at " + (pos - 1) + " in json: " + preview(text)
                    );
                }
            }
        }

        private List<Object> parseArray() {
            expect('[');
            List<Object> list = new ArrayList<>();
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return list;
            }
            while (true) {
                list.add(parseValue());
                skipWhitespace();
                char c = peek();
                pos++;
                if (c == ']') {
                    return list;
                }
                if (c != ',') {
                    throw new IllegalArgumentException(
                            "Expected ',' or ']' at " + (pos - 1) + " in json: " + preview(text)
                    );
                }
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder out = new StringBuilder();
            while (true) {
                char c = peek();
                pos++;
                if (c == '"') {
                    return out.toString();
                }
                if (c != '\\') {
                    out.append(c);
                    continue;
                }
                char escaped = peek();
                pos++;
                switch (escaped) {
                    case '"' -> out.append('"');
                    case '\\' -> out.append('\\');
                    case '/' -> out.append('/');
                    case 'b' -> out.append('\b');
                    case 'f' -> out.append('\f');
                    case 'n' -> out.append('\n');
                    case 'r' -> out.append('\r');
                    case 't' -> out.append('\t');
                    case 'u' -> {
                        if (pos + 4 > text.length()) {
                            throw new IllegalArgumentException(
                                    "Broken unicode escape in json: " + preview(text)
                            );
                        }
                        out.append((char) Integer.parseInt(text, pos, pos + 4, 16));
                        pos += 4;
                    }
                    default -> throw new IllegalArgumentException(
                            "Unknown escape '\\" + escaped + "' in json: " + preview(text)
                    );
                }
            }
        }

        private Double parseNumber() {
            int start = pos;
            while (pos < text.length()) {
                char c = text.charAt(pos);
                if ((c >= '0' && c <= '9') || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                    pos++;
                } else {
                    break;
                }
            }
            if (start == pos) {
                throw new IllegalArgumentException(
                        "Unexpected character at " + pos + " in json: " + preview(text)
                );
            }
            return Double.parseDouble(text.substring(start, pos));
        }

        private void expectWord(String word) {
            if (!text.startsWith(word, pos)) {
                throw new IllegalArgumentException(
                        "Invalid literal at " + pos + " in json: " + preview(text)
                );
            }
            pos += word.length();
        }
    }

    private static String preview(String text) {
        return text.length() <= 120 ? text : text.substring(0, 120) + "...";
    }

    // -------- WRITE --------

    static void write(@NotNull StringBuilder out, @Nullable Object value) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String string) {
            writeString(out, string);
        } else if (value instanceof Double number) {
            // keep integral values integral for the Player
            if (number == Math.floor(number) && !number.isInfinite()) {
                out.append(number.longValue());
            } else {
                out.append(number);
            }
        } else if (value instanceof Number || value instanceof Boolean) {
            out.append(value);
        } else if (value instanceof Map<?, ?> map) {
            out.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    out.append(',');
                }
                first = false;
                writeString(out, String.valueOf(entry.getKey()));
                out.append(':');
                write(out, entry.getValue());
            }
            out.append('}');
        } else if (value instanceof List<?> list) {
            out.append('[');
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    out.append(',');
                }
                write(out, list.get(i));
            }
            out.append(']');
        } else {
            throw new IllegalArgumentException(
                    "Not a json value: " + value.getClass().getName()
            );
        }
    }

    static void writeString(@NotNull StringBuilder out, @NotNull String value) {
        out.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }
}
