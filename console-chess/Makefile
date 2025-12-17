# ================= CONFIG =================
SRC_DIR := src
BIN_DIR := bin
MAIN_CLASS := ChessGame

JFLAGS := -d $(BIN_DIR)

# ================= FILES ==================
SRCS := $(shell find $(SRC_DIR) -name "*.java")

# ================= TARGETS =================
all: run clean

compile:
	mkdir -p $(BIN_DIR)
	javac $(JFLAGS) $(SRCS)

run: compile
	java -cp $(BIN_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
