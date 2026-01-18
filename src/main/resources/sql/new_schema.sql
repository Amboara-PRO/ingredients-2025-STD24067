create type unit_type as enum('PCS', 'KG', 'L');

alter table dish add column selling_price numeric(10,2);

alter table dish drop column price;

alter table ingredient drop column required_quantity;

alter table ingredient drop column id_dish;

create table DishIngredient(
  id serial primary key,
    id_dish int references dish (id),
    id_ingredient int references ingredient (id),
    quantity_required numeric(10,2),
    unit unit_type
);