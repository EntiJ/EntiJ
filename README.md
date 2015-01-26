![travis build badge](https://travis-ci.org/EntiJ/EntiJ.svg)
# EntiJ
Entity-Component based, event driven game engine and entity management system written in Java 8 with functional elements.

Here is an **example** that sets up the logic of a Tic Tac Toe game:
```java
static final long EMPTY = 0;
static final long X = 1;
static final long O = 2;
static final long RUNNING = 0;
static final long WIN = 1;
static final long DRAW = 2;
  
static enum Move {PLAY, CLEAR, RESTART, CHANGE_PLAYER};

// contains the positions of the 3 squares that made the victory
static class WinObject {List<Long> winTriple; WinObject(List<Long> wt) {winTriple = wt;}};

// for each square, this lists contains the reamaining two squares that can form
// a winning triple with it
static final List<List<Pair<Long, Long>>> WIN_PAIRS = asList(
        asList(pair(1L, 2L), pair(3L, 6L), pair(4L, 8L)),
        asList(pair(0L, 2L), pair(4L, 7L)),
        asList(pair(0L, 1L), pair(5L, 8L), pair(4L, 6L)),
        asList(pair(4L, 5L), pair(0L, 6L)),
        asList(pair(3L, 5L), pair(1L, 7L), pair(0L, 8L), pair(2L, 6L)),
        asList(pair(3L, 4L), pair(2L, 8L)),
        asList(pair(7L, 8L), pair(0L, 3L), pair(2L, 4L)),
        asList(pair(6L, 8L), pair(1L, 4L)),
        asList(pair(6L, 7L), pair(2L, 5L), pair(0L, 4L))
);
  
static Terrain setUp() {
    final Terrain t = new Terrain("Tic Tac Toe");
    t.addLogic((Entity e, Object move) -> {
        if (move == Move.CHANGE_PLAYER) {
            // if there isn't any empty square
            if (!t.inState(EMPTY).hasAny(named("square"))) {
                // the state of the Terrain should be DRAW
                return new MoveReaction().state(DRAW);
            } else {
                long currentPlayer = t.getProp("current player");
                currentPlayer = currentPlayer == X ? O : X;
                 // switch the current player
                return new MoveReaction().prop("current player", currentPlayer);
            }
        } else if (move == Move.RESTART) {
            // clear all squares 
            t.named("square").forEach((Entity sq) -> sq.move(Move.CLEAR));
            // reset state and current player
            return new MoveReaction().state(RUNNING)
                    .prop("current player", X);
        } else if (move instanceof WinObject) {
            List<Long> winTriple = ((WinObject)move).winTriple;
            // the winner is the one who owns a winning square
            long winner = t.at(winTriple.get(0)).any(named("square")).get().getState();
            return new MoveReaction().state(WIN)
                    .prop("winner", winner)
                    .prop("win triple", winTriple);
        }
        return null;
    });
    t.move(Move.RESTART);
    
    Logic squareLogic = (Entity sq, Object move) -> {
        if (move == Move.PLAY && t.getState() == RUNNING
                && sq.getState() == EMPTY) {
            long player = t.getProp("current player");
            // change the state of the square to current player
            // and then signal to terrain to change the current player
            return new MoveReaction().state(player)
                    .andThen(t, Move.CHANGE_PLAYER);
        } else if (move == Move.CLEAR) {
            return new MoveReaction().state(EMPTY);
        }
        return null;
    };
    Consumer<StateEvent> winListener = (StateEvent evt) -> {
        // called every time a square changes state to check for victory
        long sqState = evt.nextState;
        if (sqState == EMPTY) return;
        List<Pair<Long, Long>> wp = WIN_PAIRS.get((int)evt.source.getPosit());
        for (Pair<Long, Long> pair : wp) {
            // if the other two squares of the win triple are in the same state
            // (i.e. belong to the same player) we have a winner!
            if (t.at(pair.val1).hasAny(named("square").and(inState(sqState)))
                 && t.at(pair.val2).hasAny(named("square").and(inState(sqState)))) {
                t.move(new WinObject(asList(pair.val1, pair.val2, evt.source.getPosit())));
                return;
            }
        }
    };
    Component squareMaker = squareLogic
            .combine(new SimpleComponent().stateListen(winListener));
    
    for (long i = 0; i < 9; i++) {
        final Entity sq = new Entity("square", i, EMPTY);
        squareMaker.attach(sq);
        t.add(sq);
    }
    
    return t;
}
```
