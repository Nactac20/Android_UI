from fastapi import FastAPI
from pydantic import BaseModel
from typing import Dict, Any, Optional, List
import time

app = FastAPI()

analytics_events: list["AnalyticsEvent"] = []


class UIComponent(BaseModel):
    id: str
    type: str
    label: str | None = None
    action: dict | None = None
    analytics: dict | None = None
    payload: dict | None = None
    style: dict | None = None


class SecondScreenConfig(BaseModel):
    components: list[UIComponent]


class AnalyticsEvent(BaseModel):
    event: str
    params: Dict[str, str] | None = None
    timestamp: int


@app.get("/ui/second", response_model=SecondScreenConfig)
def get_second_ui():
    return SecondScreenConfig(
        components=[
            UIComponent(id="title", type="title", label="SDUI: Second screen"),
            UIComponent(id="subtitle", type="text", label="Config is delivered from server as JSON."),
            UIComponent(id="divider_1", type="divider"),
            UIComponent(id="stat_1", type="stat", label="Balance", payload={"value": "10 000 ₽"}),
            UIComponent(id="stat_2", type="stat", label="Positions", payload={"value": "8"}),
            UIComponent(
                id="toast_btn",
                type="button",
                label="Show toast",
                action={"type": "show_toast", "payload": {"message": "Hello from SDUI!"}},
                analytics={
                    "impressionEvent": "sdui_toast_button_impression",
                    "clickEvent": "sdui_toast_button_click",
                },
            ),
        ]
    )


@app.post("/analytics/events")
def post_analytics_event(event: AnalyticsEvent):
    analytics_events.append(event)
    print(
        f"[analytics] event={event.event} timestamp={event.timestamp} params={event.params or {}}"
    )
    return {"status": "ok", "received": len(analytics_events)}


@app.get("/health")
def health():
    return {"ok": True, "timestamp": int(time.time())}

