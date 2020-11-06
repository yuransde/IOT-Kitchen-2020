package iotkitchen.voicetest;

public class Recipe {

    /* Elements of a recipe */
    public String title;
    public String detail;
    public String ingredient[];
    public String instruction[];

    public Recipe(String title, String detail, String ingredient[], String instruction[])
    {
        this.title = title;
        this.detail = detail;
        this.ingredient = ingredient;
        this.instruction = instruction;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDetail()
    {
        return detail;
    }

    public String getIngredient(int i)
    {
        return ingredient[i];
    }

    public String getInstruction(int i)
    {
        return instruction[i];
    }

}
