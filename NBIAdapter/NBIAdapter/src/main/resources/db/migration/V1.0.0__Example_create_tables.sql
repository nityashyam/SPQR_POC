CREATE TABLE example_order (
    id bigint NOT NULL AUTO_INCREMENT,
    version integer NOT NULL,
    external_reference varchar(50),
    type varchar(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT example_order_external_reference_unique UNIQUE(external_reference)
);
CREATE TABLE example_order_item (
    id bigint NOT NULL AUTO_INCREMENT,
    version integer NOT NULL,
    product_name varchar(255),
    quantity integer NOT NULL,
    exampleorder_id bigint,
    PRIMARY KEY (ID),
    CONSTRAINT example_order_item_order_id_FK FOREIGN KEY(exampleorder_id) REFERENCES example_order(id)
);
