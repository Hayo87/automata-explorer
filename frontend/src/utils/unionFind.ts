export class UnionFind {
  private parent: Map<number, number> = new Map();

  add(x: number): void {
    if (!this.parent.has(x)) {
      this.parent.set(x, x);
    }
  }

  find(x: number): number {
    if (!this.parent.has(x)) {
      this.add(x);
    }
    const px = this.parent.get(x)!;
    if (px !== x) {
      const root = this.find(px);
      this.parent.set(x, root); // Path compression
      return root;
    }
    return px;
  }

  union(x: number, y: number): void {
    this.add(x);
    this.add(y);
    const rootX = this.find(x);
    const rootY = this.find(y);
    if (rootX !== rootY) {
      this.parent.set(rootX, rootY);
    }
  }

  nodes(): number[] {
    return Array.from(this.parent.keys());
  }

  groups(): Map<number, number[]> {
    const result = new Map<number, number[]>();
    for (const node of this.nodes()) {
      const root = this.find(node);
      if (!result.has(root)) {
        result.set(root, []);
      }
      result.get(root)!.push(node);
    }
    return result;
  }
}
