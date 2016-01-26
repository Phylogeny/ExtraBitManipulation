package com.phylogeny.extrabitmanipulation.reference;

public class Configs
{
	//ITEM PROPERTIES
		//BIT WRENCH
			public static boolean TAKES_DAMAGE_BIT_WRENCH;
			public static int MAX_DAMAGE_BIT_WRENCH;
		
		//SCULPTING LOOP
			public static boolean TAKES_DAMAGE_SCULPTING_LOOP;
			public static int MAX_DAMAGE_SCULPTING_LOOP;
			public static int DEFAULT_REMOVAL_RADIUS;
			public static int MAX_REMOVAL_RADIUS;
	
	//RECIPES
		//BIT WRENCH
			public static boolean RECIPE_BIT_WRENCH_IS_ENABLED;
			public static String[] RECIPE_BIT_WRENCH;
			public static String[] RECIPE_BIT_WRENCH_DEFAULT = new String[] {
						"gemDiamond", "", "gemDiamond", "", "ingotIron", "", "", "ingotIron", "" };
			public static boolean RECIPE_BIT_WRENCH_IS_SHAPED;
			public static boolean RECIPE_BIT_WRENCH_ORE_DICTIONARY;
		
		//SCULPTING LOOP
			public static boolean RECIPE_SCULPTING_LOOP_IS_ENABLED;
			public static String[] RECIPE_SCULPTING_LOOP;
			public static String[] RECIPE_SCULPTING_LOOP_DEFAULT = new String[] {
						"", "gemDiamond", "", "", "ingotIron", "", "", "ingotIron", "" };
			public static boolean RECIPE_SCULPTING_LOOP_IS_SHAPED;
			public static boolean RECIPE_SCULPTING_LOOP_ORE_DICTIONARY;
	
	//RENDER
		//WRENCH OVERLAYS
			public static boolean DISABLE_OVERLAYS;
			public static double ROTATION_PERIOD;
			public static double MIRROR_PERIOD;
			public static double MIRROR_AMPLITUDE;
			public static double TRANSLATION_SCALE_PERIOD;
			public static double TRANSLATION_DISTANCE;
			public static double TRANSLATION_OFFSET_DISTANCE;
			public static double TRANSLATION_FADE_DISTANCE;
			public static double TRANSLATION_MOVEMENT_PERIOD;
	
		//SCULPTING LOOP SPHERE
			public static boolean RENDER_OUTER_SPHERE;
			public static boolean RENDER_INNER_SPHERE;
			public static float OUTER_SPHERE_ALPHA;
			public static float INNER_SPHERE_ALPHA;
			public static float SPHERE_RED;
			public static float SPHERE_GREEN;
			public static float SPHERE_BLUE;
			public static float SPHERE_LINE_WIDTH;
	
		//SCULPTING LOOP BOX
			public static boolean RENDER_OUTER_BOX;
			public static boolean RENDER_INNER_BOX;
			public static float OUTER_BOX_ALPHA;
			public static float INNER_BOX_ALPHA;
			public static float BOX_RED;
			public static float BOX_GREEN;
			public static float BOX_BLUE;
			public static float BOX_LINE_WIDTH;
}