import evento1 from '../../assets/images/evento1.jpeg';
import evento2 from '../../assets/images/evento2.jpeg';

export const datosEventos = [
  {
    titulo: "Conferencia de Tecnología 2025",
    descripcion: "Un evento para conocer las últimas tendencias en tecnología.",
    organizador_id: 1,
    fechaInicio: "2025-10-01T09:00:00",
    duracion: 8,
    lugar: "Centro de Convenciones Buenos Aires",
    max_participantes: 300,
    min_participantes: 20,
    precio: 1500,
    tags: ["Tecnología", "Innovación"], // antes categorias
    estado: "activo",
    categoria: "tech", // antes tag
    imagen: evento1,
  },
  {
    titulo: "Feria Gastronómica Primavera",
    descripcion: "Disfruta de los mejores sabores de la estación.",
    organizador_id: 2,
    fechaInicio: "2025-10-10T12:00:00",
    duracion: 6,
    lugar: "Parque Central",
    max_participantes: 200,
    min_participantes: 10,
    precio: 800,
    tags: ["Gastronomía", "Feria"], // antes categorias
    estado: "activo",
    categoria: "food", // antes tag
    imagen: evento2,
  },
  {
    titulo: "Maratón Solidaria",
    descripcion: "Corre por una causa solidaria y ayuda a quienes más lo necesitan.",
    organizador_id: 3,
    fechaInicio: "2025-11-05T07:00:00",
    duracion: 5,
    lugar: "Costanera Norte",
    max_participantes: 500,
    min_participantes: 50,
    precio: 1000,
    tags: ["Deporte", "Solidaridad"], // antes categorias
    estado: "activo",
    categoria: "run", // antes tag
    imagen: evento1,
  },
  {
    titulo: "Expo Arte Contemporáneo",
    descripcion: "Exposición de artistas emergentes y consagrados.",
    organizador_id: 4,
    fechaInicio: "2025-09-20T15:00:00",
    duracion: 7,
    lugar: "Museo de Arte Moderno",
    max_participantes: 150,
    min_participantes: 5,
    precio: 1200,
    tags: ["Arte", "Exposición"], // antes categorias
    estado: "activo",
    categoria: "art", // antes tag
    imagen: evento2,
  },
  {
    titulo: "Torneo de Ajedrez Abierto",
    descripcion: "Participa en el torneo abierto para todas las edades.",
    organizador_id: 5,
    fechaInicio: "2025-12-02T10:00:00",
    duracion: 4,
    lugar: "Club Social y Deportivo",
    max_participantes: 100,
    min_participantes: 10,
    precio: 500,
    tags: ["Juegos", "Ajedrez"], // antes categorias
    estado: "activo",
    categoria: "chess", // antes tag
    imagen: evento1,
  },
  {
    titulo: "Festival de Música Urbana",
    descripcion: "Bandas y DJs en vivo para disfrutar la mejor música urbana.",
    organizador_id: 6,
    fechaInicio: "2025-12-15T18:00:00",
    duracion: 10,
    lugar: "Anfiteatro Municipal",
    max_participantes: 400,
    min_participantes: 30,
    precio: 2000,
    tags: ["Música", "Festival"], // antes categorias
    estado: "activo",
    categoria: "music", // antes tag
    imagen: evento2,
  },
];
