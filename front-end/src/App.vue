<template>
  <v-card theme="dark">
    <v-sheet
      v-if="productCount != null"
      class="d-flex align-center justify-center flex-wrap text-center mx-auto px-4"
      style="
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 9999;
      "
      elevation="20"
      height="50"
      rounded
      max-width="800"
      width="100%"
    >
      <div>
        <h2 class="text-h4 font-weight-black text-orange">
          NUMBER OF PRODUCTS: {{ productCount }}
        </h2>
      </div>
    </v-sheet>
    <v-layout theme="dark">
      <div @click="handleCanvasClick" id="konva-container"></div>
      <v-navigation-drawer v-model="drawer" :rail="rail" permanent app>
        <v-list density="compact" nav>
          <v-tooltip
            v-for="item in menuItems"
            :key="item.value"
            :label="item.title"
            position="right"
          >
            <template v-slot:activator="{ props }">
              <v-list-item
                style="margin-top: 180%"
                v-bind="props"
                :active="isItemActive(item.value)"
                :prepend-icon="item.icon"
                :title="item.title"
                :value="item.value"
                @click="handleMenuItemClick(item)"
              >
              </v-list-item>
            </template>
            <span>{{ item.title }}</span>
          </v-tooltip>
        </v-list>
      </v-navigation-drawer>
    </v-layout>
  </v-card>
</template>

<script>
import Konva from "konva";
import axios from "axios";
import Stomp from "stompjs";

export default {
  data() {
    return {
      stompClient: null,

      drawer: true,
      rail: true,
      menuItems: [
        { title: "Queue", value: "queue", icon: "mdi-crosshairs-question" },
        { title: "Machine", value: "machine", icon: "mdi-router-network" },
        { title: "Connect", value: "connect", icon: "mdi-arrow-top-right" },
        { title: "Play", value: "play", icon: "mdi-play-circle" },
        { title: "Increase Product", value: "add", icon: "mdi-plus-circle" },
        { title: "Decrease Product", value: "minus", icon: "mdi-minus-circle" },
        { title: "Replay", value: "replay", icon: "mdi-reload" },
        { title: "Clear", value: "clear", icon: "mdi-layers-off" },
      ],
      stage: null,
      layer: null,
      selectedShape: null,
      startShape: null,
      endShape: null,
      lastshapeclicked: null,
      productCount: 0,
      circles: [],
      squares: [],
    };
  },
  methods: {
    isItemActive(value) {
      if (
        value === "add" ||
        value === "minus" ||
        value === "play" ||
        value === "replay" ||
        value === "clear"
      ) {
        return false;
      }
      return null;
    },
    handleMenuItemClick(item) {
      if (
        item.value === this.selectedShape &&
        item.value != "add" &&
        item.value != "minus"
      ) {
        this.selectedShape = null;
      } else {
        this.selectedShape = item.value;
      }
      this.startShape = null;
      this.endShape = null;
      this.lastshapeclicked = null;
      if (this.selectedShape === "add") {
        this.addProduct();
      } else if (this.selectedShape === "minus") {
        this.removeProduct();
      } else if (this.selectedShape === "play") {
        this.play();
      } else if (this.selectedShape === "replay") {
        this.replay();
      } else if (this.selectedShape === "clear") {
        this.clear();
      }
    },
    play() {
      if (this.productCount > 0) {
        axios
          .post("http://localhost:8081/start", this.productCount, {
            headers: {
              "Content-Type": "application/json",
            },
          })
          .catch((error) => {
            console.error(error);
          });
      }
    },
    replay() {
      axios
        .get("http://localhost:8081/replay", {})
        .catch((error) => {
          console.error(error);
        });
    },
    clear() {
      axios
        .get("http://localhost:8081/clear", {})
        .then((response) => {
          console.log(response);
          this.stage.destroy();
          this.stage = new Konva.Stage({
            container: "konva-container",
            width: window.innerWidth,
            height: window.innerHeight,
          });

          this.layer = new Konva.Layer();
          this.stage.add(this.layer);
          this.circles = [];
          this.squares = [];
          this.productCount = 0;
        })
        .catch((error) => {
          console.error(error);
        });
    },
    addProduct() {
      this.productCount++;
    },
    removeProduct() {
      if (this.productCount > 0) {
        this.productCount--;
      }
    },
    handleCanvasClick(event) {
      const container = this.$el.querySelector("#konva-container");
      const rect = container.getBoundingClientRect();
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      if (this.selectedShape === "connect") {
        this.handleConnectClick(clickX, clickY);
      } else if (this.selectedShape === "queue") {
        this.drawCircle(clickX, clickY);
      } else if (this.selectedShape === "machine") {
        this.drawSquare(clickX, clickY);
      }
    },
    async drawCircle(x, y) {
      try {
        const response = await axios.get("http://localhost:8081/add/queue");
        const circleID = response.data;
        const circle = new Konva.Circle({
          x,
          y,
          radius: 25,
          fill: "orange",
          id: "Q" + circleID,
          draggable: false,
        });

        circle.on("click", () => {
          this.handleShapeClick(circle);
        });

        this.layer.add(circle);

        const text = new Konva.Text({
          x,
          y,
          text: "Q" + circleID,
          fontSize: 14,
          fill: "black",
          fontStyle: "bold",
          width: 40,
          align: "center",
        });
        text.offsetX(text.width() / 2);
        text.offsetY(text.height() / 2);

        text.on("click", () => {
          this.handleShapeClick(circle);
        });

        this.layer.add(text);

        this.stage.draw();

        this.circles.push({ id: circleID, circle, text });
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },

    async drawSquare(x, y) {
      try {
        const response = await axios.get("http://localhost:8081/add/machine");

        const squareID = response.data;
        const square = new Konva.Rect({
          x,
          y,
          width: 50,
          height: 50,
          id: "M" + squareID, 
          fill: "#808080",
          stroke: "orange",
          strokeWidth: 6,
          draggable: false,
        });

        square.on("click", () => {
          this.handleShapeClick(square);
        });

        this.layer.add(square);

        const text = new Konva.Text({
          x: x + 25,
          y: y + 25,
          text: "M" + squareID,
          fontSize: 14,
          fill: "black",
          fontStyle: "bold",
          width: 40,
          align: "center",
        });

        text.offsetX(text.width() / 2);
        text.offsetY(text.height() / 2);

        text.on("click", () => {
          this.handleShapeClick(square);
        });

        this.layer.add(text);

        this.stage.draw();

        this.squares.push({ id: squareID, square, text });
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },
    handleShapeClick(shape) {
      this.lastshapeclicked = shape;
    },
    handleConnectClick(clickX, clickY) {
      console.log(this.lastshapeclicked);
      const clickedShape = this.lastshapeclicked;
      if (clickedShape) {
        if (!this.startShape) {
          this.startShape = clickedShape;
        } else {
          this.endShape = clickedShape;
          this.connectShapes();
          this.startShape = null;
          this.endShape = null;
          this.lastshapeclicked = null;
        }
      }
    },
    drawArrow(startShape, endShape) {
      const dx = endShape.x() - startShape.x();
      const dy = endShape.y() - startShape.y();
      let angle = Math.atan2(-dy, dx);
      const radius = 48;
      let from = {
        x: startShape.x(),
        y: startShape.y(),
      };

      let to = {
        x: endShape.x(),
        y: endShape.y(),
      };

      if (startShape.getClassName() === "Rect") {
        from.x += 25;
        from.y += 25;
      }

      if (endShape.getClassName() === "Rect") {
        to.x += 25;
        to.y += 25;
      }

      const arrow = new Konva.Arrow({
        points: [
          from.x + -radius * Math.cos(angle + Math.PI),
          from.y + radius * Math.sin(angle + Math.PI),
          to.x + -radius * Math.cos(angle),
          to.y + radius * Math.sin(angle),
        ],
        fill: "white",
        stroke: "white",
        strokeWidth: 2,
      });

      this.layer.add(arrow);
      this.stage.draw();
    },
    connectShapes() {
      if (
        this.startShape &&
        this.endShape &&
        this.startShape.id()[0] !== this.endShape.id()[0]
      ) {
        const startShapeId = this.startShape.id();
        const endShapeId = this.endShape.id();
        const startShape = this.startShape;
        const endShape = this.endShape;
        axios
          .post(
            "http://localhost:8081/connect",
            {
              source: startShapeId,
              destination: endShapeId,
            },
            {
              headers: {
                "Content-Type": "application/json",
              },
            }
          )
          .then((response) => {
            this.drawArrow(startShape, endShape);
          })
          .catch((error) => {
            console.error(error);
          });
      } else {
        this.startShape = null;
        this.endShape = null;
        this.lastshapeclicked = null;
      }
    },
    update_queue(id, queueproducts) {
      const targetCircle = this.circles.find(
        (circleObj) => circleObj.id === id
      );

      if (targetCircle) {
        targetCircle.text.text(["Q" + id, queueproducts].join("\n"));
      } else {
        console.log("Circle with ID", id, "not found.");
      }

      return targetCircle.circle;
    },
    update_machine(id, color) {
      const targetmachine = this.squares.find(
        (squareObj) => squareObj.id === id
      );
      if (targetmachine) {
        targetmachine.square.fill(color);
        if (color != "#808080") targetmachine.square.stroke("black");
        else targetmachine.square.stroke("orange");
      } else {
        console.log("machine with ID", id, "not found.");
      }

      return targetmachine.square;
    },
  },
  mounted() {
    axios.get("http://localhost:8081/clear", {}).catch((error) => {
      console.error(error);
    });
    this.stage = new Konva.Stage({
      container: "konva-container",
      width: window.innerWidth,
      height: window.innerHeight,
    });

    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
    this.stompClient = Stomp.over(new WebSocket("ws://localhost:8081/ws"));

    this.stompClient.connect({}, (frame) => {

      this.stompClient.subscribe("/topic/ws/simulation", (message) => {
        const simulationUpdate = JSON.parse(message.body);
        const UpdatedQueue = simulationUpdate["queueMementos"];
        const UpdatedMachine = simulationUpdate["machineMementos"];
        UpdatedQueue.forEach((element) => {
          this.update_queue(element["id"], element["numberOfProducts"]);
        });
        UpdatedMachine.forEach((element) => {
          this.update_machine(element["id"], element["color"]);
        });
      });
    });
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  },
};
</script>


<style scoped>
</style>
