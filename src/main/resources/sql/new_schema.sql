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

///////////////////////////////////////////////////

create type movement_type as enum('IN', 'OUT');

create table stockMovement(
       id serial primary key,
       id_ingredient int references ingredient(id),
       quantity numeric(10, 2) not null,
       type mouvement_type not null,
       unit unit_type not null,
       creation_datetime timestamp not null
);