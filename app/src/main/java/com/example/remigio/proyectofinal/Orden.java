package com.example.remigio.proyectofinal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
/**
 * Created by Remigio on 22-11-2017.
 */

public class Orden {
    Map<Menu, Integer> orden;
    double m_value = 0;
    double mesa = 0;

    Orden(double mesa)

    {
        this.mesa=mesa;
        orden = new LinkedHashMap<>();
    }

    void addToCart(Menu menu)
    {
        if(orden.containsKey(menu))
            orden.put(menu, orden.get(menu) + 1);
        else
            orden.put(menu, 1);

        m_value += menu.getPrecio();
    }

    int getQuantity(Menu menu)
    {
        return orden.get(menu);
    }

    Set getProducts()
    {
        return orden.keySet();
    }

    void empty()
    {
        orden.clear();
        m_value = 0;
    }

    double getValue()
    {
        return m_value;
    }
    double getMesa()
    {
        return mesa;
    }

    int getSize()
    {
        return orden.size();
    }
}
