package com.example.uambite.model;

import com.example.uambite.exceptions.InvalidStateException;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.example.uambite.model.EstadoPedido.CANCELADO;
import static com.example.uambite.model.EstadoPedido.CONFIRMADO;
import static com.example.uambite.model.EstadoPedido.EN_CAMINO;
import static com.example.uambite.model.EstadoPedido.EN_PREPARACION;
import static com.example.uambite.model.EstadoPedido.ENTREGADO;
import static com.example.uambite.model.EstadoPedido.LISTO;
import static com.example.uambite.model.EstadoPedido.PENDIENTE;

public final class EstadoPedidoTransiciones {

    private static final Map<EstadoPedido, Set<EstadoPedido>> VALIDAS = Map.of(
            PENDIENTE, EnumSet.of(CONFIRMADO, CANCELADO),
            CONFIRMADO, EnumSet.of(EN_PREPARACION, CANCELADO),
            EN_PREPARACION, EnumSet.of(LISTO, CANCELADO),
            LISTO, EnumSet.of(EN_CAMINO),
            EN_CAMINO, EnumSet.of(ENTREGADO),
            ENTREGADO, EnumSet.noneOf(EstadoPedido.class),
            CANCELADO, EnumSet.noneOf(EstadoPedido.class)
    );

    private EstadoPedidoTransiciones() {}

    public static void validar(EstadoPedido origen, EstadoPedido destino) {
        Set<EstadoPedido> permitidos = VALIDAS.getOrDefault(origen, Set.of());
        if (!permitidos.contains(destino)) {
            throw new InvalidStateException(
                    "Transición de estado no permitida: " + origen + " -> " + destino);
        }
    }

    public static boolean puedeTransicionarA(EstadoPedido origen, EstadoPedido destino) {
        return VALIDAS.getOrDefault(origen, Set.of()).contains(destino);
    }

    public static boolean puedeSerCancelado(EstadoPedido estado) {
        return VALIDAS.getOrDefault(estado, Set.of()).contains(CANCELADO);
    }
}
