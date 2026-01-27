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

                    List<DishIngredient> ingredients = findDishIngredientByDishId(id);

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
                for (DishIngredient di : toSave.getIngredients()) {
                    psIns.setInt(1, dishId);
                    psIns.setInt(2, di.getIngredient().getId());
                    psIns.setDouble(3, di.getQuantity_required());
                    psIns.setString(4, di.getUnit().name());
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

    public Ingredient saveIngredient(Ingredient toSave) {
            Connection conn = new DBConnection().getConnection();
            try{
                PreparedStatement upsertIngredientSql = conn.prepareStatement("""
                INSERT INTO ingredient (id, name, price, category)
                VALUES (?, ?, ?, ?::ingredient_category)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    price = EXCLUDED.price,
                    category = EXCLUDED.category
                RETURNING id
                """);
                upsertIngredientSql.setObject(1, toSave.getId());
                upsertIngredientSql.setString(2, toSave.getName());
                upsertIngredientSql.setDouble(3, toSave.getPrice());
                upsertIngredientSql.setString(4, toSave.getCategory().name());

                ResultSet rs = upsertIngredientSql.executeQuery();
                rs.next();
                Integer ingredientId = rs.getInt("id");
                toSave.setId(ingredientId);

                if (toSave.getStockMovementList() != null) {
                    for (StockMovement sm : toSave.getStockMovementList()) {
                        PreparedStatement insertsm = conn.prepareStatement("""
INSERT INTO stockMovement (id, id_ingredient, quantity, type, unit, creation_datetime)
VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?)
ON CONFLICT (id) DO NOTHING
""");
                        insertsm.setObject(1, sm.getId());
                        insertsm.setInt(2, ingredientId);
                        insertsm.setDouble(3, sm.getValue().getQuantity());
                        insertsm.setString(4, sm.getType().name());
                        insertsm.setString(5, sm.getValue().getUnit().name());
                        insertsm.setTimestamp(6, Timestamp.from(sm.getCreationDatetime()));
                        insertsm.addBatch();
                        insertsm.executeUpdate();
                    }

                }

                return findIngredientById(ingredientId);
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

    public List<Ingredient> findAllIngredients() {
            DBConnection dbConnection = new DBConnection();
            Connection conn = dbConnection.getConnection();
            List<Ingredient> ingredients = new ArrayList<>();
            try {
                conn.setAutoCommit(false);
                PreparedStatement selectAllIngredientSQL = conn.prepareStatement("""
SELECT id, name, price, category from ingredient
""");
                ResultSet rs = selectAllIngredientSQL.executeQuery();
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setStockMovementList(findStockMovementById(rs.getInt("id")));
                    ingredients.add(ingredient);

                }
                dbConnection.closeConnection(conn);
                return ingredients;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    public Ingredient findIngredientById(Integer idIngredient) {
            DBConnection dbConnection = new DBConnection();
            Connection conn = dbConnection.getConnection();
            try {
                PreparedStatement selectIngredientSQL = conn.prepareStatement("""
SELECT id, name, price, category from ingredient where id = ?
""");
                selectIngredientSQL.setObject(1, idIngredient);
                ResultSet rs = selectIngredientSQL.executeQuery();
                while (rs.next()){
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setStockMovementList(findStockMovementById(idIngredient));

                    return ingredient;

                }
                dbConnection.closeConnection(conn);
                throw new RuntimeException("Ingredient not found");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public List<StockMovement> findStockMovementById(Integer idIngredient) {
            DBConnection dbConnection = new DBConnection();
            Connection conn = dbConnection.getConnection();
            List<StockMovement> stockMovements = new ArrayList<>();

            try {
                PreparedStatement selectStockMovementSQL = conn.prepareStatement("""
    select id, quantity, type, unit, creation_datetime
    from stockmovement where id_ingredient = ?
""");
                selectStockMovementSQL.setObject(1, idIngredient);
                ResultSet rs = selectStockMovementSQL.executeQuery();
                while (rs.next()) {
                    StockValue stockValue = new StockValue();
                    stockValue.setQuantity(rs.getDouble("quantity"));
                    stockValue.setUnit(UnitEnum.valueOf(rs.getString("unit")));

                    StockMovement stockMovement = new StockMovement();
                    stockMovement.setId(rs.getInt("id"));
                    stockMovement.setValue(stockValue);
                    stockMovement.setType(MouvementTypeEnum.valueOf(rs.getString("type")));
                    stockMovement.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                    stockMovements.add(stockMovement);
                }
                return stockMovements;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    public List<DishIngredient> findDishIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> ingredients = new ArrayList<>();

        String query = """
        SELECT 
            i.id AS ingredient_id,
            i.name AS ingredient_name,
            i.price AS ingredient_price,
            i.category AS ingredient_category,
            di.quantity_required,
            di.unit
        FROM dishIngredient di
        JOIN ingredient i ON di.id_ingredient = i.id
        WHERE id_dish = ?
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("ingredient_id"));
                ingredient.setName(resultSet.getString("ingredient_name"));
                ingredient.setPrice(resultSet.getObject("ingredient_price") == null
                        ? null
                        : resultSet.getDouble("ingredient_price"));
                String categoryStr = resultSet.getString("ingredient_category");
                ingredient.setCategory(categoryStr == null ? null : CategoryEnum.valueOf(categoryStr.toUpperCase()));

                DishIngredient di = new DishIngredient();
                di.setIngredient(ingredient);
                di.setQuantity_required(resultSet.getObject("quantity_required") == null
                        ? null
                        : resultSet.getDouble("quantity_required"));
                String unitStr = resultSet.getString("unit");
                di.setUnit(unitStr == null ? null : UnitEnum.valueOf(unitStr.toUpperCase()));

                ingredients.add(di);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients du plat", e);
        } finally {
            dbConnection.closeConnection(connection);
        }

        return ingredients;
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
