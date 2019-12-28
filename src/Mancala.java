import java.util.Scanner;
import java.util.Random; //for basic AI opponent

public class Mancala {
  static Scanner userInput = new Scanner(System.in);
  public int[] gameBoard = new int[]{4,4,4,4,4,4,0,4,4,4,4,4,4,0}; //indices 0-5 are player0, 6-12 are player1
  public int playerNumber = 0;

  public static void main(String[] args){
    int landed = -1;
    int clearSide = -1;
    Random rand = new Random();
    Mancala gameMaster = new Mancala();

    //GAME START
    while(clearSide == -1){

      //gameMaster.ShowBoard();
      //player0 turn
      if (gameMaster.playerNumber == 0){
        //get player input
        do{
          //System.out.print("Enter the pit you'd like to pick up from: ");
          //landed = gameMaster.MakeMove(userInput.nextInt());
          landed = gameMaster.MakeMove(rand.nextInt(6));
        }while(landed == -1);
      }
      if (gameMaster.playerNumber == 1){
        //computer turn aka player1
        do {
          landed = gameMaster.MakeMove(rand.nextInt(6));
        }while(landed == -1);
      }
      //check for capture
      if(gameMaster.CheckForCapture(landed)){
        gameMaster.Capture(landed);
      }
      //check to see if the player gets to go again
      if (!gameMaster.landedMancala(landed)){
        gameMaster.playerNumber = (gameMaster.playerNumber + 1) % 2; //alternate players
      }
      clearSide = gameMaster.CheckForClear();
    }
    //END SEQUENCE

    //collect remaining points
    gameMaster.collect(clearSide);
    //gameMaster.ShowBoard();
    gameMaster.PrintWinner();


  }

  public void PrintWinner(){
    if (this.gameBoard[6] == this.gameBoard[13]){
      System.out.println("Tie Game");
    }
    else if (this.gameBoard[6] > this.gameBoard[13]){
      System.out.println("Player 1 Wins");
    }
    if (this.gameBoard[6] < this.gameBoard[13]){
      System.out.println("Player 2 Wins");
    }
  }

  // Input: a number between 0 and 5 representing the pits from left to right
  // Return: the index that the last marble went in
  //          -1 = error
  public int MakeMove(int index){
    int skipMancala = 13;
    int circularIndex = -1;
    int offset = 0;
    int marbles;

    if (index > 5 || index < 0){
      //TODO: fix this error with a function before this one is called
      System.out.println("you cannot pick up from your mancala. Choose from one of the six pits on your side.");
      return -1;
    }
    else{
      if(this.playerNumber == 1){  //offset the index for the opponents side and update the mancala
        index += 7;
        skipMancala = 6;
      }

      System.out.printf("Player# %d, picks pit# %d\n", this.playerNumber, index);

      //get the number of marbles in the pit and count down from there
      if (this.gameBoard[index] == 0){
        //TODO: this error could be handled before calling this function by using a check empty function
        //System.out.println("You cannot choose an empty mancala");
        return -1;
      }
      else{
        marbles = gameBoard[index];
        this.gameBoard[index] = 0;
      }
      for(int i = 0; i < marbles; i++){
        circularIndex = (index + i + offset + 1) % 14; //create an index that will loop back to 0 instead of overflowing

        //in the case that you need to skip placing a marble in the opponents mancala
        // we add one to offset and decrement 1 from the number of marbles to drop 'i'
        if (circularIndex == skipMancala){
          offset += 1;
          i--;
          continue;
        }
        gameBoard[circularIndex] += 1;
      }
    }
    return circularIndex;
  }

  //returns true if the current player captures opponents pieces
  //this happens if a player lands in an empty pit on their side of the board
  public boolean CheckForCapture(int landed){
    //player0 captures
    if (this.playerNumber == 0 && landed > -1 && landed < 6){
      if (this.gameBoard[landed] == 1) {
        return true;
      }
    }
    //player2 captures
    if (this.playerNumber == 1 && landed > 6 && landed < 13){
      if (this.gameBoard[landed] == 1) {
        return true;
      }
    }
    return false;
  }

  public boolean landedMancala(int landed){
    if (this.playerNumber == 0 && landed == 6) {
      return true;
    }
    if (this.playerNumber == 1 && landed == 13) {
      return true;
    }
    else{
      return false;
    }
  }

  public void Capture(int landed){
    int captured = 1; //get the point for the one landed on your side
    this.gameBoard[landed] = 0; // collect the one landed on your side
    captured += this.gameBoard[12 - landed]; //get the points for the ones opposite yours
    this.gameBoard[12 - landed] = 0; //collect the ones opposite

    if (playerNumber == 0){
      this.gameBoard[6] += captured;  // add them to player0 mancala
      System.out.printf("Player# 0 captures %d points\n",captured);
    }
    else{
      this.gameBoard[13] += captured;  // add them to player1 mancala
      System.out.printf("Player# 1 captures %d points\n",captured);
    }
  }

  //Returns: -1 for no clear side
  //          0 for top side clear
  //          1 for bottom side clear
  public int CheckForClear(){
    int endState = -1;
    //check top
    for (int i = 0; i < 6; i++){
      if(this.gameBoard[i] != 0){
        endState = -1;
        break;
      }
      else{
        endState = 0;
      }
    }
    //check bottom
    if (endState == -1){
      for (int i = 7; i < 13; i++){
        if (this.gameBoard[i] != 0){
          endState = -1;
          break;
        }
        else{
          endState = 1;
        }
      }
    }
    return endState;
  }

  //input: what side is clear
  //collects remaining points and adds them to the proper mancala
  public void collect(int clearSide){
    int collect = 0;
    if (clearSide == 0){
      for (int i = 7; i < 13; i++){
        collect += this.gameBoard[i];
        this.gameBoard[i] = 0;
      }
      this.gameBoard[13] += collect;
    }
    else{
      for (int i = 0; i < 6; i++){
        collect += this.gameBoard[i];
        this.gameBoard[i] = 0;
      }
      this.gameBoard[6] += collect;
    }

  }
  //prints the current state of the board
  public void ShowBoard(){
    System.out.println(" ___________{5}_____{4}_____{3}_____{2}_____{1}_____{0}____________");
    System.out.println("|  ____     ____    ____    ____    ____    ____    ____    ____  |");
    System.out.printf("| |    |   [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  |    | |\n",
            this.gameBoard[5], this.gameBoard[4], this.gameBoard[3],
            this.gameBoard[2], this.gameBoard[1], this.gameBoard[0]);
    System.out.println("| |    |                                                   |    | |");
    System.out.printf("| | %2d |    ____    ____    ____    ____    ____    ____   | %2d | |\n",
            this.gameBoard[6], this.gameBoard[13]);
    System.out.printf("| |____|   [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  [_%2d_]  |____| |\n",
            this.gameBoard[7], this.gameBoard[8], this.gameBoard[9],
            this.gameBoard[10], this.gameBoard[11], this.gameBoard[12]);
    System.out.println("|_________________________________________________________________|");
  }
}
