    // const char *bdf_font_file = "fonts/mspgothic_042623.bdf";
    // rgb_matrix::Font font; /* Load font. This needs to be a filename with a bdf bitmap font. */
    // if (!font.LoadFont( bdf_font_file )) {
    //     fprintf(stderr, "Couldn't load font '%s'\n", bdf_font_file);
    //     exit( 1 ); }
    // bool with_outline = false;
    // rgb_matrix::Font *outline_font = NULL;                            // If we want an outline around the font,
    // if ( with_outline ) { outline_font = font.CreateOutlineFont(); }  // we create a new font with the original font
    //                                                                   // as a template that is just an outline font.
    // RGBMatrix *canvas = RGBMatrix::CreateFromOptions( _matrix_options, _runtime_opt );
    // if ( canvas == NULL ) {
    //     printf( "Couldn't create RGBMatrix object.  exiting... \n" );
    //     exit( 1 ); }
    // Color color(255, 255, 0);
    // Color bg_color(0, 0, 0);
    // Color flood_color(0, 0, 0);
    // Color outline_color( 0,0,0 );
    // const bool all_extreme_colors = (_matrix_options.brightness == 100)
    //     && this->FullSaturation( color ) && this->FullSaturation( bg_color ) && this->FullSaturation( outline_color );
    // if ( all_extreme_colors ) { canvas->SetPWMBits( 1 ); }
    // const int x = 0;
    // int y       = 0;

    // canvas->Fill(flood_color.r, flood_color.g, flood_color.b); // clear screen
    // Color firstRowColor( 0, 255, 0 );
    // rgb_matrix::Font period_font;
    // if (!period_font.LoadFont( "fonts/mspgothic_030623.bdf" )) {
    //     fprintf( stderr, "Couldn't load font '%s'.  exiting... \n", "fonts/mspgothic_030623.bdf" );
    //     exit( 1 ); }

    // #define BIG_NUMBER_FONT "fonts/fgm_27_ee.bdf"
    // rgb_matrix::Font big_number_font;
    // if (!big_number_font.LoadFont( BIG_NUMBER_FONT )) {
    //     fprintf( stderr, "Couldn't load font '%s'. exiting... \n", BIG_NUMBER_FONT );
    //     exit( 1 ); }

    // #define SPACE_BEFORE_1ST_NUMBER 16
    // #define SPACE_BEFORE_2ND_NUMBER 2
    // #define SPACE_BEFORE_SMALL_NUMBER 7
    // #define SPACE_BETWEEN_SMALL_NUMBERS 17
    // #define COORDS_FOR_SECOND_NUMBER 42
    // #define SPACE_BETWEEN_FIRST_NUMBERS 38
    // #define PIPE_SPACE 1
    // #define LETTER_SPACING 1
    // Color pipe_color( 255, 255, 0 );
    // rgb_matrix::DrawText( canvas, big_number_font, x + PIPE_SPACE, y + big_number_font.baseline(), pipe_color,  outline_font ? NULL : &bg_color, "I", 0 /* letter_spacing */ );
    // rgb_matrix::DrawText( canvas, big_number_font, x + SPACE_BEFORE_1ST_NUMBER, y + big_number_font.baseline() - 1, firstRowColor, outline_font ? NULL : &bg_color, "8", LETTER_SPACING );
    // rgb_matrix::DrawText( canvas, big_number_font, x + SPACE_BETWEEN_FIRST_NUMBERS, y + big_number_font.baseline() - 1, firstRowColor, outline_font ? NULL : &bg_color, "9", LETTER_SPACING );
    // y += big_number_font.height();
    // printf("Font height before second row: %d\n", big_number_font.height());
    // rgb_matrix::DrawText( canvas, big_number_font, x + PIPE_SPACE, y + big_number_font.baseline() + 2, pipe_color,  outline_font ? NULL : &bg_color, "I", 0 /* letter_spacing */ );
    // Color secondRowColor( 255, 0, 0 );
    // rgb_matrix::DrawText( canvas, big_number_font, x + SPACE_BEFORE_1ST_NUMBER, y + big_number_font.baseline() - 1, firstRowColor, outline_font ? NULL : &bg_color, "A", LETTER_SPACING );
    // rgb_matrix::DrawText( canvas, big_number_font, x + COORDS_FOR_SECOND_NUMBER, y + big_number_font.baseline() - 1, firstRowColor, outline_font ? NULL : &bg_color, "d", LETTER_SPACING );

    // y += big_number_font.height() + 4;
    // printf("Font height before third row: %d\n", big_number_font.height());

    // #define LITTLE_NUMBER_FONT "fonts/little_numbers.bdf"
    // rgb_matrix::Font little_number_font;
    // if (!little_number_font.LoadFont( LITTLE_NUMBER_FONT )) {
    //     fprintf( stderr, "Couldn't load font '%s'\n", LITTLE_NUMBER_FONT );
    //     exit( 1 ); }
    // Color thirdRowColor( 0, 255, 0 );
    // rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER, y + little_number_font.baseline(), thirdRowColor,  outline_font ? NULL : &bg_color, "1 2 3", 0 /* letter_spacing */ );
    // y += little_number_font.height() - 5;
    // printf("Font height before fourth row: %d\n", little_number_font.height());
    // Color fourthRowColor( 255, 0, 0 );
    // rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER, y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &bg_color, "4", 0 /* letter_spacing */ );
    // rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER + SPACE_BETWEEN_SMALL_NUMBERS, y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &bg_color, "5", 0 /* letter_spacing */ );
    // rgb_matrix::DrawText( canvas, little_number_font, x + SPACE_BEFORE_SMALL_NUMBER + (( 2 * SPACE_BETWEEN_SMALL_NUMBERS )), y + little_number_font.baseline(), fourthRowColor, outline_font ? NULL : &bg_color, "6", 0 /* letter_spacing */ );
    //delete canvas; // Finished. Shut down the RGB matrix.
