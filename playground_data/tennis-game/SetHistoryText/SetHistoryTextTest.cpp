#include "../googletest/googletest/include/gtest/gtest.h"
#include "./SetHistoryText.h"

class SetHistoryTextTest : public ::testing::Test {
 protected:
    SetHistoryTextTest() {}           // You can do set-up work for each test here.
    ~SetHistoryTextTest() override {} // You can do clean-up work that doesn't throw exceptions here.
       
    void SetUp() override { // Code here will be called immediately after the constructor (right before each test).
        gameState = new GameState();
        setHistoryText = new SetHistoryText( gameState ); }

    void TearDown() override { // Code here will be called immediately after each test (right before the destructor).
        delete setHistoryText;
        delete gameState; }

    GameState* gameState;
    SetHistoryText* setHistoryText; };

TEST_F( SetHistoryTextTest, TestGetSetHistoryText ) {
    std::map< int, int > set_history = {{ 0, 15 }, { 1, 10 }, { 2, 40 }};
    gameState->setPlayer1SetHistory( set_history );
    set_history = {{ 0, 4 }, { 1, 5 }, { 2, 30 }};
    gameState->setPlayer2SetHistory( set_history );    
    std::string result = setHistoryText->getSetHistoryText( 1 );
    ASSERT_EQ(result, "15 10 40 ");
    result = setHistoryText->getSetHistoryText( 2 );
    ASSERT_EQ(result, "4 5 30 "); }

// int main(int argc, char** argv) {
//     ::testing::InitGoogleTest(&argc, argv);
//     return RUN_ALL_TESTS(); }
