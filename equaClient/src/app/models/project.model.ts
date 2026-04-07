export interface Project {
  id: number;
  name: string;
  description: string;
  modelUrl?: string;
  category?: string;
  imageUrl?: string;
  features?: string[];
  status?: string;
}
