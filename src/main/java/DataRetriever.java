import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {
        public Dish findDishById(Integer id) {
            DBConnection dbConnection = new DBConnection();
            Connection connection = dbConnection.getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        """
                                select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as dish_price
                                from dish
                                where dish.id = ?;
                                """);
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Dish dish = new Dish();
                    dish.setId(resultSet.getInt("dish_id"));
                    dish.setName(resultSet.getString("dish_name"));
                    dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                    dish.setPrice(resultSet.getObject("dish_price") == null
                            ? null : resultSet.getDouble("dish_price"));

                    List<Ingredient> ingredients = findIngredientByDishId(id);
                    for (Ingredient ingredient : ingredients) {
                        ingredient.setDish(dish);
                    }
                    dish.setIngredients(ingredients);
                    return dish;
                }
                dbConnection.closeConnection(connection);
                throw new RuntimeException("Dish not found " + id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
        INSERT INTO dish (id, selling_price, name, dish_type)
        VALUES (?, ?, ?, ?::dish_type)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            dish_type = EXCLUDED.dish_type,
            selling_price = EXCLUDED.selling_price
        RETURNING id
    """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }

                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }

                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            try (PreparedStatement psDel = conn.prepareStatement(
                    "DELETE FROM dishIngredient WHERE id_dish = ?"
            )) {
                psDel.setInt(1, dishId);
                psDel.executeUpdate();
            }

            String insertDishIngredient = """
            INSERT INTO dishIngredient (id_dish, id_ingredient, quantity_required, unit)
            VALUES (?, ?, ?, ?::unit_type)
        """;

            try (PreparedStatement psIns = conn.prepareStatement(insertDishIngredient)) {
                for (Ingredient ing : toSave.getIngredients()) {
                    psIns.setInt(1, dishId);
                    psIns.setInt(2, ing.getId());
                    psIns.setDouble(3, ing.getQuantity());
                    psIns.setString(4, ing.getUnit().name());
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();

            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, price, category)
                        VALUES (?, ?, ?, ?::ingredient_category)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setDouble(3, ingredient.getPrice());
                    ps.setString(4, ingredient.getCategory().name());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    public List<Ingredient> findIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
            SELECT i.id,
                   i.name,
                   i.price,
                   i.category,
                   di.quantity_required,
                   di.unit,
                   d.id AS dish_id,
                   d.name AS dish_name,
                   d.dish_type,
                   d.selling_price AS dish_price
            FROM dishIngredient di
            JOIN ingredient i ON di.id_ingredient = i.id
            JOIN dish d ON di.id_dish = d.id
            WHERE di.id_dish = ?
        """);

            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
                ingredient.setQuantity(resultSet.getDouble("quantity_required"));
                ingredient.setUnit(UnitEnum.valueOf(resultSet.getString("unit")));

                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));

                ingredient.setDish(dish);

                ingredients.add(ingredient);
            }
            dbConnection.closeConnection(connection);

            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    public int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}
