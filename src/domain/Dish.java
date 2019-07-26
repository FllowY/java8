package domain;

/**
 * 菜
 * @author rongtao
 * create date:2018年10月17日 上午9:28:19
 */
public class Dish {
	//菜名
	private final String name;
	//是否是素菜
	private final boolean vegetarian;
	//卡路里
	private final int calories;
	//类型枚举
	private final Type type;

	/**
	 * MEAT 肉
	 * FISH 鱼
	 * OTHER 其他
	 */
	public enum Type { MEAT, FISH, OTHER }

	public Dish(String name, boolean vegetarian, int calories, Type type) {
		this.name = name;
		this.vegetarian = vegetarian;
		this.calories = calories;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public int getCalories() {
		return calories;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}
}
