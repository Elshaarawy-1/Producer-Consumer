<template>
  <v-card>
    <v-layout>
    <div @click="handleCanvasClick" id="konva-container"></div>
      <v-navigation-drawer
        v-model="drawer"
        :rail="rail"
        permanent
        app
      >
        <v-list density="compact" nav>
          <v-tooltip v-for="item in menuItems" :key="item.value" :label="item.title" position="right">
            <template v-slot:activator="{ props }">
              <v-list-item v-bind="props" :prepend-icon="item.icon" :title="item.title" :value="item.value" @click="handleMenuItemClick(item)">
              </v-list-item>
            </template>
            <span>{{item.value}}</span>
          </v-tooltip>
        </v-list>
      </v-navigation-drawer>
    </v-layout>
  </v-card>
</template>

<script>
import Konva from 'konva';

export default {
  data() {
    return {
      drawer: true,
      rail: true,
      menuItems: [
        { title: 'Queue', value: 'queue', icon: 'mdi-crosshairs-question' },
        { title: 'Machine', value: 'machine', icon: 'mdi-router-network' },
        { title: 'Connect', value: 'connect', icon: 'mdi-arrow-top-right' },
        { title: 'Play', value: 'play', icon: 'mdi-play-circle' },
        { title: 'Reload', value: 'reload', icon: 'mdi-reload' },
      ],
      stage: null,
      layer: null,
      selectedShape: null,
      startShape: null,
      endShape: null,

      
    };
  },
  methods: {
    handleMenuItemClick(item) {
      this.selectedShape = item.value;

      // Clear start and end shapes when a menu item is clicked
      this.startShape = null;
      this.endShape = null;
    },
    handleCanvasClick(event) {
      const container = this.$el.querySelector('#konva-container');
      const rect = container.getBoundingClientRect();
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      if (this.selectedShape === 'connect') {
        this.handleConnectClick(clickX, clickY);
      } else if (this.selectedShape === 'queue') {
        this.drawCircle(clickX, clickY);
      } else if (this.selectedShape === 'machine') {
        this.drawSquare(clickX, clickY);
      }
    },
    drawCircle(x, y) {
      //request backend and get the queueID and put it below text
      const circleID = 0; // replace it with the return data from backend
      const circle = new Konva.Circle({
        x,
        y,
        radius: 25,
        fill: 'orange',
        draggable: false,
      });

      this.layer.add(circle);
      const text = new Konva.Text({
          x: x , // Adjust the x-coordinate based on your design
          y: y, // Adjust the y-coordinate based on your design
          text: 'Q' + circleID,
          fontSize: 12,
          fill: 'black',
          width: 40, // Set the width of the text box
          align: 'center',
      });

      // Center the text inside the circle
      text.offsetX(text.width() / 2);
      text.offsetY(text.height() / 2);

      // Add the text to the same layer
      this.layer.add(text);
      this.stage.draw();
    },
    drawSquare(x, y) {
      //request backend and get the machineID and put it below text
      const squareID = 1; // replace it with the return data from backend
      const square = new Konva.Rect({
        x,
        y,
        width: 50,
        height: 50,
        fill: 'grey',
        draggable: false,
      });

      this.layer.add(square);
      const text = new Konva.Text({
          x: x+25 , // Adjust the x-coordinate based on your design
          y: y+25, // Adjust the y-coordinate based on your design
          text: 'M' + squareID,
          fontSize: 12,
          fill: 'white',
          width: 40, // Set the width of the text box
          align: 'center',
      });

      // Center the text inside the circle
      text.offsetX(text.width() / 2);
      text.offsetY(text.height() / 2);

      // Add the text to the same layer
      this.layer.add(text);
      this.stage.draw();
    },
    handleConnectClick(clickX, clickY) {
      const clickedShape = this.findShapeAtPosition(clickX, clickY);
      if (clickedShape) {
        if (!this.startShape) {
          this.startShape = clickedShape;
        } else {
          this.endShape = clickedShape;
          this.connectShapes();
          this.startShape = null;
          this.endShape = null;
          this.selectedShape = null;
        }
      }
    },
    findShapeAtPosition(x, y) {
      return this.layer.getChildren(node => node.getClassName() === 'Circle' || node.getClassName() === 'Rect').find(shape => {
        const shapeX = shape.x();
        const shapeY = shape.y();
        const shapeWidth = shape.getClassName() === 'Circle' ? shape.radius() : shape.width();
        const shapeHeight = shape.getClassName() === 'Circle' ? shape.radius() : shape.height();

        return x >= shapeX && x <= shapeX + shapeWidth && y >= shapeY && y <= shapeY + shapeHeight;
      });
    },
    connectShapes() {
      if (this.startShape && this.endShape) {
        const arrow = new Konva.Arrow({
          points: [
            this.startShape.x() + this.startShape.width() / 2,
            this.startShape.y() + this.startShape.height() / 2,
            this.endShape.x() + this.endShape.width() / 2,
            this.endShape.y() + this.endShape.height() / 2,
          ],
          fill: 'black',
          stroke: 'black',
          strokeWidth: 2,
        });

        this.layer.add(arrow);
        this.stage.draw();
      }
    },
  },
  mounted() {
    this.stage = new Konva.Stage({
      container: 'konva-container',
      width: window.innerWidth,
      height: window.innerHeight,
    });

    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
  },
};
</script>
